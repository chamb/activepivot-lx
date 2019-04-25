CREATE TABLE PRODUCTS (
  Id INTEGER not NULL,
  ProductName VARCHAR(255),
  ProductType VARCHAR(255), 
  UnderlierCode VARCHAR(255),
  UnderlierCurrency VARCHAR(255),
  UnderlierType VARCHAR(255),
  UnderlierValue DOUBLE,
  ProductBaseMtm DOUBLE,
  BumpedMtmUp DOUBLE,
  BumpedMtmDown DOUBLE,
  Theta DOUBLE,
  Rho DOUBLE,
  PRIMARY KEY ( Id )
);

CREATE TABLE TRADES (
  Id BIGINT not NULL,
  ProductId INTEGER,
  ProductQtyMultiplier DOUBLE,
  Desk VARCHAR(255),
  Book INTEGER,
  Trader VARCHAR(255),
  Counterparty VARCHAR(255),
  Date DATE,
  Status VARCHAR(255),
  IsSimulated VARCHAR(255),
  PRIMARY KEY ( Id )
);

CREATE TABLE RISKS (
  TradeId BIGINT not NULL,
  Pnl DOUBLE,
  Delta DOUBLE,
  PnlDelta DOUBLE,
  Gamma DOUBLE,
  Vega DOUBLE,
  PnlVega DOUBLE
);
