/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import java.util.Optional;

/**
 * sql keywords
 * http://www.postgres.cn/docs/11/sql-keywords-appendix.html
 *
 * @author truthbean
 * @since 0.1.0
 */
public enum SqlKeywords {
    A,
    ABORT,
    ABS,
    ABSENT,
    ABSOLUTE,
    ACCESS,
    ACCESSIBLE,
    ACCORDING,
    ACCOUNT,
    ACTION,
    ADA,
    ADD,
    ADMIN,
    AFTER,
    AGAINST,
    ALGORITHM,
    AGGREGATE,
    ALL,
    ALLOCATE,
    ALSO,
    ALTER,
    ALWAYS,
    ANALYSE,
    ANALYZE,
    AND,
    ANY,
    ARE,
    ARRAY,
    ARRAY_AGG,
    ARRAY_MAX_CARDINALITY,
    AS,
    ASC,
    ASCII,
    ASENSITIVE,
    ASSERTION,
    ASSIGNMENT,
    ASYMMETRIC,
    AT,
    ATOMIC,
    ATTACH,
    ATTRIBUTE,
    ATTRIBUTES,
    AUTHORIZATION,
    AUTOINCREMENT,
    AUTO_INCREMENT,
    AVG,
    AVG_ROW_LENGTH,
    BACKWARD,
    BASE64,
    BEFORE,
    BEGIN,
    BEGIN_FRAME,
    BEGIN_PARTITION,
    BERNOULLI,
    BETWEEN,
    BIGINT,
    BINARY,
    BINLOG,
    BIT,
    BIT_LENGTH,
    BLOB,
    BLOCK,
    BLOCKED,
    BOM,
    BOOL,
    BOOLEAN,
    BOTH,
    BTREE,
    BREADTH,
    BY,
    BYTE,
    C,
    CACHE,
    CALL,
    CALLED,
    CARDINALITY,
    CASCADE,
    CASCADED,
    CASE,
    CAST,
    CATALOG,
    CATALOG_NAME,
    CEIL,
    CEILING,
    CHAIN,
    CHAR,
    CHARACTER,
    CHARACTERISTICS,
    CHARACTERS,
    CHARACTER_LENGTH,
    CHARACTER_SET_CATALOG,
    CHARACTER_SET_NAME,
    CHARACTER_SET_SCHEMA,
    CHAR_LENGTH,
    CHARSET,
    CHECK,
    CHECKPOINT,
    CHECKSUM,
    CIPHER,
    CLASS,
    CLASS_ORIGIN,
    CLIENT,
    CLOB,
    CLOSE,
    CLUSTER,
    COALESCE,
    COBOL,
    CODE,
    COLLATE,
    COLLATION,
    COLLATION_CATALOG,
    COLLATION_NAME,
    COLLATION_SCHEMA,
    COLLECT,
    COLUMN,
    COLUMNS,
    COLUMN_FORMAT,
    COLUMN_NAME,
    COMMAND_FUNCTION,
    COMMAND_FUNCTION_CODE,
    COMMENT,
    COMMENTS,
    COMMIT,
    COMMITTED,
    CONCURRENTLY,
    CONCURRENT,
    CONDITION,
    CONDITION_NUMBER,
    CONFIGURATION,
    CONFLICT,
    COMPACT,
    COMPLETION,
    COMPRESSED,
    COMPRESSION,
    CONNECT,
    CONNECTION,
    CONNECTION_NAME,
    CONSTRAINT,
    CONSTRAINTS,
    CONSTRAINT_CATALOG,
    CONSTRAINT_NAME,
    CONSTRAINT_SCHEMA,
    CONSTRUCTOR,
    CONTAINS,
    CONTENT,
    CONTINUE,
    CONTROL,
    CONVERSION,
    CONVERT,
    COPY,
    CORR,
    CORRESPONDING,
    COST,
    COUNT,
    COVAR_POP,
    COVAR_SAMP,
    CREATE,
    CROSS,
    CSV,
    CUBE,
    CUME_DIST,
    CURRENT,
    CURRENT_CATALOG,
    CURRENT_DATE,
    CURRENT_DEFAULT_TRANSFORM_GROUP,
    CURRENT_PATH,
    CURRENT_ROLE,
    CURRENT_ROW,
    CURRENT_SCHEMA,
    CURRENT_TIME,
    CURRENT_TIMESTAMP,
    CURRENT_TRANSFORM_GROUP_FOR_TYPE,
    CURRENT_USER,
    CURSOR,
    CURSOR_NAME,
    CYCLE,
    DATA,
    DATABASE,
    DATABASES,
    DATALINK,
    DATE,
    DATETIME_INTERVAL_CODE,
    DATETIME_INTERVAL_PRECISION,
    DAY,
    DB,
    DEALLOCATE,
    DEC,
    DECIMAL,
    DECLARE,
    DEFAULT,
    DEFAULTS,
    DEFERRABLE,
    DEFERRED,
    DEFINED,
    DEFINER,
    DEGREE,
    DELETE,
    DELIMITER,
    DELIMITERS,
    DENSE_RANK,
    DEPENDS,
    DEPTH,
    DEREF,
    DERIVED,
    DESC,
    DESCRIBE,
    DESCRIPTOR,
    DETACH,
    DETERMINISTIC,
    DIAGNOSTICS,
    DICTIONARY,
    DISABLE,
    DISCARD,
    DISCONNECT,
    DISPATCH,
    DISTINCT,
    DLNEWCOPY,
    DLPREVIOUSCOPY,
    DLURLCOMPLETE,
    DLURLCOMPLETEONLY,
    DLURLCOMPLETEWRITE,
    DLURLPATH,
    DLURLPATHONLY,
    DLURLPATHWRITE,
    DLURLSCHEME,
    DLURLSERVER,
    DLVALUE,
    DO,
    DOCUMENT,
    DOMAIN,
    DOUBLE,
    DROP,
    DYNAMIC,
    DYNAMIC_FUNCTION,
    DYNAMIC_FUNCTION_CODE,
    EACH,
    ELEMENT,
    ELSE,
    ELSEIF,
    EMPTY,
    ENABLE,
    ENCODING,
    ENCLOSED,
    ENCRYPTED,
    ENCRYPTION,
    END,
    END_EXEC("END-EXEC"),
    END_FRAME,
    END_PARTITION,
    ENDS,
    ENGINE,
    ENGINES,
    ENFORCED,
    ENUM,
    ERROR,
    ERRORS,
    EQUALS,
    ESCAPE,
    ESCAPED,
    EVENT,
    EVENTS,
    EVERY,
    EXCEPT,
    EXCEPTION,
    EXCHANGE,
    EXCLUDE,
    EXCLUDING,
    EXCLUSIVE,
    EXEC,
    EXECUTE,
    EXISTS,
    EXIT,
    EXP,
    EXPANSION,
    EXPIRE,
    EXPLAIN,
    EXPORT,
    EXPRESSION,
    EXTENDED,
    EXTENT_SIZE,
    EXTENSION,
    EXTERNAL,
    EXTRACT,
    FALSE,
    FAST,
    FAMILY,
    FAULTS,
    FETCH,
    FILE,
    FILTER,
    FINAL,
    FIRST,
    FIRST_VALUE,
    FLAG,
    FLOAT,
    FLOOR,
    FOLLOWING,
    FOR,
    FORCE,
    FOREIGN,
    FORTRAN,
    FORWARD,
    FOUND,
    FRAME_ROW,
    FREE,
    FREEZE,
    FROM,
    FS,
    FULL,
    FUNCTION,
    FUNCTIONS,
    FUSION,
    G,
    GENERAL,
    GENERATED,
    GET,
    GLOBAL,
    GO,
    GOTO,
    GRANT,
    GRANTED,
    GREATEST,
    GROUP,
    GROUPING,
    GROUPS,
    HANDLER,
    HAVING,
    HEADER,
    HEX,
    HIERARCHY,
    HOLD,
    HOUR,
    ID,
    IDENTITY,
    IF,
    IGNORE,
    ILIKE,
    IMMEDIATE,
    IMMEDIATELY,
    IMMUTABLE,
    IMPLEMENTATION,
    IMPLICIT,
    IMPORT,
    IN,
    INCLUDE,
    INCLUDING,
    INCREMENT,
    INDENT,
    INDEX,
    INDEXES,
    INDICATOR,
    INHERIT,
    INHERITS,
    INITIALLY,
    INLINE,
    INNER,
    INOUT,
    INPUT,
    INSENSITIVE,
    INSERT,
    INSTANCE,
    INSTANTIABLE,
    INSTEAD,
    INT,
    INTEGER,
    INTEGRITY,
    INTERSECT,
    INTERSECTION,
    INTERVAL,
    INTO,
    INVOKER,
    IS,
    ISNULL,
    ISOLATION,
    JOIN,
    K,
    KEY,
    KEY_MEMBER,
    KEY_TYPE,
    LABEL,
    LAG,
    LANGUAGE,
    LARGE,
    LAST,
    LAST_VALUE,
    LATERAL,
    LEAD,
    LEADING,
    LEAKPROOF,
    LEAST,
    LEFT,
    LENGTH,
    LEVEL,
    LIBRARY,
    LIKE,
    LIKE_REGEX,
    LIMIT,
    LINK,
    LISTEN,
    LN,
    LOAD,
    LOCAL,
    LOCALTIME,
    LOCALTIMESTAMP,
    LOCATION,
    LOCATOR,
    LOCK,
    LOCKED,
    LOGGED,
    LOWER,
    M,
    MAP,
    MAPPING,
    MATCH,
    MATCHED,
    MATERIALIZED,
    MAX,
    MAXVALUE,
    MAX_CARDINALITY,
    MEMBER,
    MERGE,
    MESSAGE_LENGTH,
    MESSAGE_OCTET_LENGTH,
    MESSAGE_TEXT,
    METHOD,
    MIN,
    MINUTE,
    MINVALUE,
    MOD,
    MODE,
    MODIFIES,
    MODULE,
    MONTH,
    MORE,
    MOVE,
    MULTISET,
    MUMPS,
    NAME,
    NAMES,
    NAMESPACE,
    NATIONAL,
    NATURAL,
    NCHAR,
    NCLOB,
    NESTING,
    NEW,
    NEXT,
    NFC,
    NFD,
    NFKC,
    NFKD,
    NIL,
    NO,
    NONE,
    NORMALIZE,
    NORMALIZED,
    NOT,
    NOTHING,
    NOTIFY,
    NOTNULL,
    NOWAIT,
    NTH_VALUE,
    NTILE,
    NULL,
    NULLABLE,
    NULLIF,
    NULLS,
    NUMBER,
    NUMERIC,
    OBJECT,
    OCCURRENCES_REGEX,
    OCTETS,
    OCTET_LENGTH,
    OF,
    OFF,
    OFFSET,
    OIDS,
    OLD,
    ON,
    ONLY,
    OPEN,
    OPERATOR,
    OPTION,
    OPTIONS,
    OR,
    ORDER,
    ORDERING,
    ORDINALITY,
    OTHERS,
    OUT,
    OUTER,
    OUTPUT,
    OVER,
    OVERLAPS,
    OVERLAY,
    OVERRIDING,
    OWNED,
    OWNER,
    P,
    PAD,
    PARALLEL,
    PARAMETER,
    PARAMETER_MODE,
    PARAMETER_NAME,
    PARAMETER_ORDINAL_POSITION,
    PARAMETER_SPECIFIC_CATALOG,
    PARAMETER_SPECIFIC_NAME,
    PARAMETER_SPECIFIC_SCHEMA,
    PARSER,
    PARTIAL,
    PARTITION,
    PASCAL,
    PASSING,
    PASSTHROUGH,
    PASSWORD,
    PATH,
    PERCENT,
    PERCENTILE_CONT,
    PERCENTILE_DISC,
    PERCENT_RANK,
    PERIOD,
    PERMISSION,
    PLACING,
    PLANS,
    PLI,
    POLICY,
    PORTION,
    POSITION,
    POSITION_REGEX,
    POWER,
    PRECEDES,
    PRECEDING,
    PRECISION,
    PREPARE,
    PREPARED,
    PRESERVE,
    PRIMARY,
    PRIOR,
    PRIVILEGES,
    PROCEDURAL,
    PROCEDURE,
    PROCEDURES,
    PROGRAM,
    PUBLIC,
    PUBLICATION,
    QUOTE,
    RANGE,
    RANK,
    READ,
    READS,
    REAL,
    REASSIGN,
    RECHECK,
    RECOVERY,
    RECURSIVE,
    REF,
    REFERENCES,
    REFERENCING,
    REFRESH,
    REGR_AVGX,
    REGR_AVGY,
    REGR_COUNT,
    REGR_INTERCEPT,
    REGR_R2,
    REGR_SLOPE,
    REGR_SXX,
    REGR_SXY,
    REGR_SYY,
    REINDEX,
    RELATIVE,
    RELEASE,
    RENAME,
    REPEATABLE,
    REPLACE,
    REPLICA,
    REQUIRING,
    RESET,
    RESPECT,
    RESTART,
    RESTORE,
    RESTRICT,
    RESULT,
    RETURN,
    RETURNED_CARDINALITY,
    RETURNED_LENGTH,
    RETURNED_OCTET_LENGTH,
    RETURNED_SQLSTATE,
    RETURNING,
    RETURNS,
    REVOKE,
    RIGHT,
    ROLE,
    ROLLBACK,
    ROLLUP,
    ROUTINE,
    ROUTINES,
    ROUTINE_CATALOG,
    ROUTINE_NAME,
    ROUTINE_SCHEMA,
    ROW,
    ROWS,
    ROW_COUNT,
    ROW_NUMBER,
    RULE,
    SAVEPOINT,
    SCALE,
    SCHEMA,
    SCHEMAS,
    SCHEMA_NAME,
    SCOPE,
    SCOPE_CATALOG,
    SCOPE_NAME,
    SCOPE_SCHEMA,
    SCROLL,
    SEARCH,
    SECOND,
    SECTION,
    SECURITY,
    SELECT,
    SELECTIVE,
    SELF,
    SENSITIVE,
    SEQUENCE,
    SEQUENCES,
    SERIALIZABLE,
    SERVER,
    SERVER_NAME,
    SESSION,
    SESSION_USER,
    SET,
    SETOF,
    SETS,
    SHARE,
    SHOW,
    SIMILAR,
    SIMPLE,
    SIZE,
    SKIP,
    SMALLINT,
    SNAPSHOT,
    SOME,
    SOURCE,
    SPACE,
    SPECIFIC,
    SPECIFICTYPE,
    SPECIFIC_NAME,
    SQL,
    SQLCODE,
    SQLERROR,
    SQLEXCEPTION,
    SQLSTATE,
    SQLWARNING,
    SQRT,
    STABLE,
    STACKED,
    STANDALONE,
    START,
    STARTING,
    STARTS,
    STATE,
    STATEMENT,
    STATIC,
    STATISTICS,
    STATS_AUTO_RECALC,
    STATS_PERSISTENT,
    STATS_SAMPLE_PAGES,
    STATUS,
    STDDEV_POP,
    STDDEV_SAMP,
    STDIN,
    STDOUT,
    STOP,
    STORAGE,
    STORED,
    STRAIGHT_JOIN,
    STRICT,
    STRING,
    STRIP,
    STRUCTURE,
    STYLE,
    SUBCLASS_ORIGIN,
    SUBMULTISET,
    SUBSCRIPTION,
    SUBJECT,
    SUBSTRING,
    SUBSTRING_REGEX,
    SUBPARTITION,
    SUBPARTITIONS,
    SUCCEEDS,
    SUM,
    SUPER,
    SUSPEND,
    SWAPS,
    SWITCHES,
    SYMMETRIC,
    SYSID,
    SYSTEM,
    SYSTEM_TIME,
    SYSTEM_USER,
    T,
    TABLE,
    TABLES,
    TABLESAMPLE,
    TABLESPACE,
    TABLE_CHECKSUM,
    TABLE_NAME,
    TEMP,
    TEMPLATE,
    TEMPORARY,
    TEMPTABLE,
    TERMINATED,
    TEXT,
    THAN,
    THEN,
    TIES,
    TIME,
    TIMESTAMP,
    TIMESTAMPADD,
    TIMESTAMPDIFF,
    TIMEZONE_HOUR,
    TIMEZONE_MINUTE,
    TINYBLOB,
    TINYINT,
    TINYTEXT,
    TO,
    TOKEN,
    TOP_LEVEL_COUNT,
    TRAILING,
    TRANSACTION,
    TRANSACTIONS_COMMITTED,
    TRANSACTIONS_ROLLED_BACK,
    TRANSACTION_ACTIVE,
    TRANSFORM,
    TRANSFORMS,
    TRANSLATE,
    TRANSLATE_REGEX,
    TRANSLATION,
    TREAT,
    TRIGGER,
    TRIGGERS,
    TRIGGER_CATALOG,
    TRIGGER_NAME,
    TRIGGER_SCHEMA,
    TRIM,
    TRIM_ARRAY,
    TRUE,
    TRUNCATE,
    TRUSTED,
    TYPE,
    TYPES,
    UESCAPE,
    UNBOUNDED,
    UNCOMMITTED,
    UNDEFINED,
    UNDER,
    UNDO,
    UNDOFILE,
    UNDO_BUFFER_SIZE,
    UNENCRYPTED,
    UNICODE,
    UNINSTALL,
    UNION,
    UNIQUE,
    UNKNOWN,
    UNLINK,
    UNLISTEN,
    UNLOCK,
    UNLOGGED,
    UNNAMED,
    UNNEST,
    UNSIGNED,
    UNTIL,
    UNTYPED,
    UPDATE,
    UPGRADE,
    UPPER,
    URI,
    USAGE,
    USE,
    USER,
    USER_DEFINED_TYPE_CATALOG,
    USER_DEFINED_TYPE_CODE,
    USER_DEFINED_TYPE_NAME,
    USER_DEFINED_TYPE_SCHEMA,
    USER_RESOURCES,
    USE_FRM,
    USING,
    UTC_DATE,
    UTC_TIME,
    UTC_TIMESTAMP,
    VACUUM,
    VALID,
    VALIDATE,
    VALIDATOR,
    VALUE,
    VALUES,
    VALUE_OF,
    VARBINARY,
    VARCHAR,
    VARCHARACTER,
    VARIADIC,
    VARYING,
    VAR_POP,
    VAR_SAMP,
    VERBOSE,
    VARIABLES,
    VERSION,
    VERSIONING,
    VIEW,
    VIEWS,
    VIRTUAL,
    VOLATILE,
    WAIT,
    WARNINGS,
    WEEK,
    WEIGHT_STRING,
    WHEN,
    WHENEVER,
    WHERE,
    WHILE,
    WHITESPACE,
    WIDTH_BUCKET,
    WINDOW,
    WITH,
    WITHIN,
    WITHOUT,
    WORK,
    WRAPPER,
    WRITE,
    X509,
    XA,
    XID,
    XML,
    XMLAGG,
    XMLATTRIBUTES,
    XMLBINARY,
    XMLCAST,
    XMLCOMMENT,
    XMLCONCAT,
    XMLDECLARATION,
    XMLDOCUMENT,
    XMLELEMENT,
    XMLEXISTS,
    XMLFOREST,
    XMLITERATE,
    XMLNAMESPACES,
    XMLPARSE,
    XMLPI,
    XMLQUERY,
    XMLROOT,
    XMLSCHEMA,
    XMLSERIALIZE,
    XMLTABLE,
    XMLTEXT,
    XMLVALIDATE,
    XOR,
    YEAR,
    YEAR_MONTH,
    YES,
    ZEROFILL,
    ZONE;

    private String value;

    SqlKeywords() {
    }

    SqlKeywords(String value) {
        this.value = value;
    }

    public String value() {
        if (value == null)
            return name();
        return value;
    }

    public static Optional<SqlKeywords> of(String name) {
        SqlKeywords[] values = values();
        for (SqlKeywords value : values) {
            if (name.equalsIgnoreCase(value.value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
