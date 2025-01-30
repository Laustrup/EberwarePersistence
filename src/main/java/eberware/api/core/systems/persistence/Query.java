package eberware.api.core.systems.persistence;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class Query {

    @Setter
    private String _script;

    private List<Parameter> _parameters;

    @Getter
    private static final char _identifier = '€';

    @Getter
    private static final char _endExpression = ' ';

    public Query(String script) {
        this(script, new ArrayList<>());
    }

    public Query(String script, Parameter parameter) {
        this(script, List.of(parameter));
    }

    public Query(Collection<Query> queries) {
        this(
                queries.stream()
                        .map(Query::get_script)
                        .reduce(String::concat)
                        .orElseThrow()
        );
    }

    public Query(String script, List<Parameter> parameters) {
        _script = String.format(script, parameters.stream().map(Parameter::get_key).toArray());
        _parameters = parameters;
    }

    public static String formatKey(String key) {
        return String.format(
                "%s%s%s",
                Query.get_identifier(),
                key,
                Query.get_endExpression()
        );
    }

    public static String valuesInsertCollection(int parameterCount) {
        List<String> parameters = new ArrayList<>();

        for (int i = 0; i < parameterCount; i++)
            parameters.add("%s");

        return String.format("(%s)", String.join(", ", parameters));
    }

    public static String formatIndexedKey(String parameter, int index) {
        return String.format("%s_%s", parameter, index).replace(" ", "") + " ";
    }

    void prepareTransaction() {
        boolean insertSemicolon = !_script
                .replace(" ", "")
                .replace("\n", "")
                .endsWith(";");

        _script = /*language=mysql*/
                "\nstart transaction;\n\n" +
                _script +
                (insertSemicolon ? ";" : "") +
                "\ncommit;";
    }

    @Getter
    public static class Parameter {

        private Integer _index;

        private String _title;

        private String _key;

        public Parameter(String title) {
            _title = title;
            _key = title.contains(String.valueOf(Query.get_identifier())) ? title : Query.formatKey(title);
        }

        public Parameter(Integer index, String parameter) {
            _index = index;
            _title = parameter;
            _key = _index == null ? _title : formatIndexedKey(parameter, _index);
        }

        @Override
        public String toString() {
            return _key;
        }
    }

    @Override
    public String toString() {
        return _script;
    }
}
