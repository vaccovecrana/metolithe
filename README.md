# metolithe

A minimal, opinionated, flat key/value JDBC object storage framework.

## 3.0 Functionality

- [x] No support for composite primary keys (explain why).
- [x] Unique constraints (only two levels supported for now).
- [x] Composite index attributes.
- [x] Static DAO code generation.
- [x] Pagination support.

## Documentation topics

- [ ] How are primary key fields assigned and extracted.
- [ ] Strategies for choosing primary key component fields (or not at all).
- [ ] `int` vs `Integer`, when to use which?
- [ ] How to handle schema migrations with generated change sets.
- [ ] Always make sure that your schema class names do not clash with database keywords.
- [ ] If you do not specify unique constraint tags on your classes, you are responsible for managing each object's primary key values.

Classes which do not define a primary key field can only be saved, loaded or deleted using one of its
attributes. Therefore, calls to `update` or `upsert` on a DAO instance will fail.

This restriction is by design in this framework.

# Resources

- https://blog.jooq.org/faster-sql-paging-with-jooq-using-the-seek-method
