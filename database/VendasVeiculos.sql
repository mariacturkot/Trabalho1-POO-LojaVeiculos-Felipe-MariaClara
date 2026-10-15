CREATE DATABASE "VendasVeiculos";

DROP TABLE IF EXISTS Venda;
DROP TABLE IF EXISTS Veiculo;
DROP TABLE IF EXISTS Funcionario;

CREATE TABLE Funcionario (
                             idFuncionario SERIAL,
                             nome VARCHAR(80) NOT NULL,
                             cpf CHAR(11) NOT NULL UNIQUE,
                             cargo VARCHAR(40) NOT NULL,
                             turno VARCHAR(20) NOT NULL,
                             salario NUMERIC(10,2) NOT NULL,

                             CONSTRAINT PK_Funcionario PRIMARY KEY (idFuncionario),
                             CONSTRAINT CK_Funcionario_CPF CHECK (LENGTH(TRIM(cpf)) = 11),
                             CONSTRAINT CK_Funcionario_Salario CHECK (salario > 0)
);

CREATE TABLE Veiculo (
                         idVeiculo SERIAL,
                         marca VARCHAR(40) NOT NULL,
                         placa CHAR(7) NOT NULL UNIQUE,
                         cor VARCHAR(30) NOT NULL,
                         valor NUMERIC(12,2) NOT NULL,
                         status VARCHAR(20) DEFAULT 'DISPONIVEL',
                         tipo VARCHAR(30) NOT NULL,
                         modelo VARCHAR(60) NOT NULL,
                         ano INTEGER NOT NULL,
                         especificacoes TEXT,

                         CONSTRAINT PK_Veiculo PRIMARY KEY (idVeiculo),
                         CONSTRAINT CK_Veiculo_Placa CHECK (LENGTH(TRIM(placa)) = 7),
                         CONSTRAINT CK_Veiculo_Valor CHECK (valor > 0),
                         CONSTRAINT CK_Veiculo_Ano CHECK (ano >= 1900),
                         CONSTRAINT CK_Veiculo_Status CHECK (
                             status IN ('DISPONIVEL', 'VENDIDO', 'RESERVADO')
                             )
);

CREATE TABLE Venda (
                       idVenda SERIAL,
                       idFuncionario INTEGER NOT NULL,
                       idVeiculo INTEGER NOT NULL UNIQUE,
                       dataVenda DATE DEFAULT CURRENT_DATE,
                       valorTotal NUMERIC(12,2) NOT NULL,
                       dataPagamento DATE,

                       CONSTRAINT PK_Venda PRIMARY KEY (idVenda),

                       CONSTRAINT FK_Venda_Funcionario
                           FOREIGN KEY (idFuncionario)
                               REFERENCES Funcionario(idFuncionario)
                               ON DELETE NO ACTION
                               ON UPDATE CASCADE,

                       CONSTRAINT FK_Venda_Veiculo
                           FOREIGN KEY (idVeiculo)
                               REFERENCES Veiculo(idVeiculo)
                               ON DELETE NO ACTION
                               ON UPDATE CASCADE,

                       CONSTRAINT CK_Venda_ValorTotal CHECK (valorTotal > 0),

                       CONSTRAINT CK_Venda_DataPagamento CHECK (
                           dataPagamento IS NULL
                               OR dataPagamento >= dataVenda
                           )
);

INSERT INTO Funcionario
(nome, cpf, cargo, turno, salario)
VALUES
    ('Carlos Henrique Silva', '12345678901', 'Vendedor', 'MANHA', 3500.00),
    ('Fernanda Oliveira', '23456789012', 'Vendedora', 'TARDE', 3800.00),
    ('Lucas Martins', '34567890123', 'Vendedor', 'MANHA', 3400.00),
    ('Mariana Souza', '45678901234', 'Gerente', 'INTEGRAL', 6500.00),
    ('Rafael Almeida', '56789012345', 'Vendedor', 'TARDE', 3600.00),
    ('Juliana Mendes', '67890123456', 'Vendedora', 'MANHA', 3700.00),
    ('Bruno Carvalho', '78901234567', 'Vendedor', 'TARDE', 3550.00),
    ('Patricia Lima', '89012345678', 'Supervisora', 'INTEGRAL', 5200.00),
    ('Diego Ferreira', '90123456789', 'Vendedor', 'MANHA', 3450.00),
    ('Amanda Ribeiro', '01234567890', 'Vendedora', 'TARDE', 3650.00);

INSERT INTO Veiculo
(marca, placa, cor, valor, status, tipo, modelo, ano, especificacoes)
VALUES
    ('Toyota', 'ABC1D23', 'Prata', 89900.00, 'VENDIDO', 'Sedan', 'Corolla XEI', 2021, 'Automatico, 2.0, flex, ar-condicionado'),
    ('Honda', 'DEF4G56', 'Preto', 82900.00, 'VENDIDO', 'Sedan', 'Civic EXL', 2020, 'Automatico, 2.0, bancos em couro'),
    ('Volkswagen', 'GHI7J89', 'Branco', 72900.00, 'DISPONIVEL', 'Hatch', 'Polo Highline', 2022, 'Automatico, 1.0 TSI, central multimidia'),
    ('Chevrolet', 'JKL1M23', 'Vermelho', 65900.00, 'VENDIDO', 'Hatch', 'Onix Premier', 2021, 'Automatico, 1.0 turbo'),
    ('Jeep', 'NOP4Q56', 'Cinza', 119900.00, 'DISPONIVEL', 'SUV', 'Compass Longitude', 2022, 'Automatico, 1.3 turbo'),
    ('Hyundai', 'RST7U89', 'Prata', 78900.00, 'RESERVADO', 'Sedan', 'HB20S Platinum', 2023, 'Automatico, 1.0 turbo'),
    ('Fiat', 'VWX1Y23', 'Branco', 67900.00, 'DISPONIVEL', 'Picape', 'Strada Freedom', 2022, 'Manual, 1.3 flex'),
    ('Renault', 'ZAB4C56', 'Azul', 58900.00, 'VENDIDO', 'Hatch', 'Sandero GT Line', 2020, 'Manual, 1.6 flex'),
    ('Nissan', 'CDE7F89', 'Preto', 84900.00, 'DISPONIVEL', 'SUV', 'Kicks Advance', 2022, 'Automatico CVT, 1.6 flex'),
    ('Ford', 'FGH1I23', 'Cinza', 97900.00, 'DISPONIVEL', 'SUV', 'Territory SEL', 2021, 'Automatico, turbo, teto solar'),
    ('Toyota', 'IJK4L56', 'Branco', 95900.00, 'VENDIDO', 'SUV', 'Corolla Cross XR', 2022, 'Automatico, 2.0, flex'),
    ('Volkswagen', 'MNO7P89', 'Azul', 109900.00, 'DISPONIVEL', 'SUV', 'T-Cross Comfortline', 2023, 'Automatico, 1.0 TSI'),
    ('Chevrolet', 'QRS1T23', 'Prata', 72900.00, 'RESERVADO', 'Sedan', 'Onix Plus Premier', 2022, 'Automatico, 1.0 turbo'),
    ('Fiat', 'UVW4X56', 'Vermelho', 62900.00, 'VENDIDO', 'Hatch', 'Argo Trekking', 2021, 'Manual, 1.3 flex'),
    ('Hyundai', 'YZA7B89', 'Branco', 89900.00, 'DISPONIVEL', 'SUV', 'Creta Action', 2021, 'Automatico, 1.6 flex'),
    ('Jeep', 'BCD1E23', 'Preto', 139900.00, 'VENDIDO', 'SUV', 'Commander Limited', 2022, 'Automatico, turbo, sete lugares'),
    ('Honda', 'EFG4H56', 'Cinza', 77900.00, 'DISPONIVEL', 'Hatch', 'City Hatch EXL', 2022, 'Automatico CVT, 1.5 flex'),
    ('Renault', 'HIJ7K89', 'Prata', 69900.00, 'DISPONIVEL', 'SUV', 'Duster Iconic', 2021, 'Automatico, 1.6 flex'),
    ('Nissan', 'LMN1O23', 'Vermelho', 92900.00, 'VENDIDO', 'SUV', 'Kicks Exclusive', 2023, 'Automatico CVT, bancos em couro'),
    ('Ford', 'PQR4S56', 'Branco', 74900.00, 'DISPONIVEL', 'Picape', 'Ranger XLS', 2019, 'Manual, diesel, cabine dupla'),
    ('Volkswagen', 'TUV7W89', 'Preto', 69900.00, 'VENDIDO', 'Hatch', 'Virtus Comfortline', 2021, 'Automatico, 1.0 TSI'),
    ('Chevrolet', 'XYZ1A23', 'Azul', 83900.00, 'DISPONIVEL', 'SUV', 'Tracker LT', 2022, 'Automatico, 1.0 turbo'),
    ('Fiat', 'CBA4D56', 'Cinza', 72900.00, 'RESERVADO', 'SUV', 'Pulse Drive', 2023, 'Automatico, 1.3 flex'),
    ('Toyota', 'FED7G89', 'Prata', 115900.00, 'VENDIDO', 'Picape', 'Hilux SR', 2020, 'Automatico, diesel, cabine dupla'),
    ('Honda', 'HGF1I23', 'Branco', 99900.00, 'DISPONIVEL', 'SUV', 'HR-V EX', 2022, 'Automatico CVT, 1.8 flex');

INSERT INTO Venda
(idFuncionario, idVeiculo, dataVenda, valorTotal, dataPagamento)
VALUES
    (1, 1, '2026-01-15', 87500.00, '2026-01-15'),
    (2, 2, '2026-02-03', 81000.00, '2026-02-05'),
    (1, 4, '2026-02-18', 64000.00, '2026-02-18'),
    (3, 8, '2026-03-07', 57000.00, '2026-03-10'),
    (6, 11, '2026-03-22', 94000.00, '2026-03-22'),
    (5, 14, '2026-04-11', 61000.00, '2026-04-12'),
    (7, 16, '2026-05-02', 136500.00, '2026-05-02'),
    (2, 19, '2026-05-28', 91000.00, '2026-05-30'),
    (9, 21, '2026-06-14', 68500.00, '2026-06-14'),
    (3, 24, '2026-07-03', 113000.00, '2026-07-05');