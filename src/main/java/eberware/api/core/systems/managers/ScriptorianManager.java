package eberware.api.core.systems.managers;

import eberware.api.core.systems.libaries.PathLibrary;
import eberware.api.core.systems.persistence.DatabaseParameter;
import eberware.api.core.systems.persistence.Scriptorian;
import eberware.api.core.systems.services.FileService;
import eberware.api.core.systems.services.JDBCService;
import eberware.api.core.systems.services.queries.ScriptorianQueryService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eberware.api.core.systems.services.ManagerService.databaseInteraction;
import static eberware.api.core.systems.persistence.queries.ScriptorianQueries.Parameter;

public class ScriptorianManager {

    private static final Logger _logger = Logger.getLogger(ScriptorianManager.class.getSimpleName());

    public static void onStartup() {
        _logger.log(
                Level.INFO,
                "Scriptorian started"
        );

        List<File> scripts = namingConvention();

        databaseInteraction(() -> {
            ScriptorianQueryService.createDefaultSchemaIfNotExists();
            List<Scriptorian.Scriptory> scriptoriesWithoutSuccess = scriptoriesWithoutSuccess();
            if (!scriptoriesWithoutSuccess.isEmpty())
                throw new IllegalStateException(String.format("""
                        %nThere is a conflict with scriptories, please resolve it.
                        
                        conflict to resolve is:
                        %s
                        
                        It can either be done by deleting the row with successtamp of null, which will make it run again on next startup or by setting it to now(), which will ignore the script on startup.
                        """,
                        scriptoriesWithoutSuccess.getFirst().get_errorMessage()
                ));

            String currentFileName = "";
            File currentFile = null;

            try {
                for (File file : findScriptsNotRecorded(scripts, buildScriptories(ScriptorianQueryService.findAllScriptories()))) {
                    currentFile = file;
                    currentFileName = file.getName();

                    ScriptorianQueryService.executeScript(FileService.getContent(file));
                    ScriptorianQueryService.putScriptory(generateParameters(currentFile, null));

                    _logger.log(
                            Level.INFO,
                            String.format("Successfully migrated file \"%s\"!", currentFileName)
                    );
                }
            } catch (Exception exception) {
                ScriptorianQueryService.putScriptory(generateParameters(currentFile, exception.getMessage()));

                _logger.log(
                        Level.WARNING,
                        String.format("Conflict with file \"%s\"", currentFileName),
                        exception.getMessage()
                );
                System.exit(5);
            }
        });
    }

    private static List<File> namingConvention() {
        StringBuilder filesRenamed = new StringBuilder();

        List<File> scripts = getScripts();

        scripts.forEach(script -> {
            if (!fileNamedAccepted(script.getName())) {
                filesRenamed.append(script.getName());
                rename(script);
            }
        });

        boolean filesHasBeenRenamed = !filesRenamed.isEmpty();

        if (filesHasBeenRenamed)
            _logger.log(Level.INFO, "Renamed " + String.join(filesRenamed.toString(), " - "));

        return filesHasBeenRenamed ? getScripts() : scripts;
    }

    private static List<File> getScripts() {
        try {
            return FileService.getFiles(PathLibrary.get_migrationDirectoryFullPath()).toList();
        } catch (FileNotFoundException e) {
            _logger.log(Level.SEVERE, "Scriptorian script not found", e);
            System.exit(3);
        }

        return null;
    }

    private static boolean fileNamedAccepted(String fileName) {
        char[] chars = fileName.toCharArray();

        return chars.length > 20 && chars[0] == 'V' && chars[20] == '|';
    }

    private static void rename(File file) {
        try {
            Files.move(
                    file.toPath(),
                    Paths.get(PathLibrary.get_migrationDirectoryFullPath() + String.format(
                            "V%s|%s",
                            LocalDateTime.ofInstant(Instant.now(), ZoneId.of("GMT"))
                                    .truncatedTo(ChronoUnit.SECONDS)
                                    .format(DateTimeFormatter.ISO_DATE_TIME),
                            file.getName()
                    )),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            _logger.log(
                    Level.SEVERE,
                    "Failed to rename file " + file.getName() + " within the Scriptorian process",
                    e
            );
            System.exit(4);
        }
    }

    private static List<Scriptorian.Scriptory> scriptoriesWithoutSuccess() {
        return buildScriptories(ScriptorianQueryService.findScriptoriesWithoutSuccess());
    }

    private static List<File> findScriptsNotRecorded(List<File> scripts, List<Scriptorian.Scriptory> scriptories) {
        List<String> fileNames = scriptories.stream()
                .map(Scriptorian.Scriptory::get_fileName)
                .toList();

        return scripts.stream()
                .filter(script -> !fileNames.contains(script.getName()))
                .toList();
    }

    private static List<DatabaseParameter> generateParameters(File file, String errorMessage) {
        if (file == null)
            return null;

        String[] name = file.getName().split("\\|");

        return List.of(
                new DatabaseParameter(Parameter.TITLE.get_key(), name[1]),
                new DatabaseParameter(Parameter.FILE_NAME.get_key(), file.getName()),
                new DatabaseParameter(Parameter.ERROR_MESSAGE.get_key(), errorMessage),
                new DatabaseParameter(Parameter.CONTENT.get_key(), FileService.getContent(file)),
                new DatabaseParameter(Parameter.VERSIONSTAMP.get_key(), LocalDateTime.parse(
                        name[0].substring(1).replace('T', ' '),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )),
                new DatabaseParameter(Parameter.SUCCESSTAMP.get_key(), errorMessage == null ? Instant.now() : null)
        );
    }

    private static Scriptorian.Scriptory buildScriptory(ResultSet resultSet) {
        return buildScriptories(resultSet).getFirst();
    }

    private static List<Scriptorian.Scriptory> buildScriptories(ResultSet resultSet) {
        return JDBCService.build(
                resultSet,
                () -> {
                    try {
                        return new Scriptorian.Scriptory(
                                resultSet.getString(Scriptorian.Scriptory.DatabaseColumns.title.name()),
                                resultSet.getString(Scriptorian.Scriptory.DatabaseColumns.file_name.name()),
                                resultSet.getString(Scriptorian.Scriptory.DatabaseColumns.error_message.name()),
                                resultSet.getString(Scriptorian.Scriptory.DatabaseColumns.content.name()),
                                JDBCService.get(
                                        resultSet.getTimestamp(Scriptorian.Scriptory.DatabaseColumns.versionstamp.name()),
                                        Timestamp::toInstant
                                ),
                                JDBCService.get(
                                        resultSet.getTimestamp(Scriptorian.Scriptory.DatabaseColumns.successtamp.name()),
                                        Timestamp::toInstant
                                ),
                                JDBCService.get(
                                        resultSet.getTimestamp(Scriptorian.Scriptory.DatabaseColumns.timestamp.name()),
                                        Timestamp::toInstant
                                )
                        );
                    } catch (SQLException exception) {
                        throw new RuntimeException(exception);
                    }
                }
        );
    }
}
