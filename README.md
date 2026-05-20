# Moya

Moya is a full-stack web application featuring a Java backend coupled with a React frontend.

The goal of the project is to design an application that will handle control & management of gas station object.
System uses *PostgreSQL* database for exchanging data. Database was divided into several modules:
- **Fuel Module** (fuel types, amount of fuel and their prices, e.g.),
- **Product Module** (food, tobacco, vehicle accesories, e.g.)
- **Receipt Module** (collecting purchase history, e.g.),
- **Supplier Module** (about the supplier, deadlines, delivery dates, e.g.),
- **User Module** (roles, access to the system, e.g.),
- **Account Module** (total balance, balance history, e.g.).

Tables scheme is presented below:
![database_scheme](additionals/databaseScheme.svg)