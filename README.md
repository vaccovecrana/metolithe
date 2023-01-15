# metolithe

A minimal, opinionated, flat key/value JDBC object storage framework.

## 2.0 Functionality

- [x] No support for composite primary keys (explain why).
- [x] Unique constraints.
- [x] Composite index attributes.
- [x] Static DAO code generation.
- [ ] Pagination support.

## Documentation topics

- [ ] How are primary key fields assigned and extracted.
- [ ] Strategies for choosing primary key component fields (or not at all).
- [ ] `int` vs `Integer`, when to use which?
- [ ] How to handle schema migrations with Liquibase generated change sets.
- [ ] Always make sure that your schema class names do not clash with database keywords.

Classes which do not define a primary key field can only be saved, loaded or deleted using one of its
attributes. Therefore, calls to `update` or `merge` on a DAO instance will fail.

This restriction is by design in this framework.

# Disclaimer

> This project is not production ready, and still requires security and code correctness audits. You use this
> software at your own risk. Vaccove Crana, LLC., its affiliates and subsidiaries waive any and all liability for any
> damages caused to you by your usage of this software.
