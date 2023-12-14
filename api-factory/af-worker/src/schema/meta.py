from enum import Enum, auto


class ParamFieldType(Enum):
    STRING = 'str'
    INTEGER = 'int'
    FLOAT = 'float'


typed_map = {
    'string': ParamFieldType.STRING,
    'str': ParamFieldType.STRING,
    'varchar': ParamFieldType.STRING,
    'char': ParamFieldType.STRING,
    'int': ParamFieldType.INTEGER,
    'integer': ParamFieldType.INTEGER,
    'float': ParamFieldType.FLOAT,
    'double': ParamFieldType.FLOAT
}