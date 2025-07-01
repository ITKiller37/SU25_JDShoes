-- === XÓA DỮ LIỆU CŨ (xóa con trước cha) ===
DELETE FROM Account;
DELETE FROM AddressShipping;
DELETE FROM Customer;
DELETE FROM Role;

-- === ROLE ===
SET IDENTITY_INSERT Role ON;
INSERT INTO Role (id, name, createDate, updateDate)
VALUES
    (1, 'ROLE_ADMIN', GETDATE(), GETDATE()),
    (2, 'ROLE_EMPLOYEE', GETDATE(), GETDATE()),
    (3, 'ROLE_USER', GETDATE(), GETDATE());
SET IDENTITY_INSERT Role OFF;

-- === CUSTOMER ===
SET IDENTITY_INSERT Customer ON;
INSERT INTO Customer (id, code, name, phoneNumber, email, deleted)
VALUES
    (1, 'KH001', N'Nguyễn Văn Mận', '099000076', 'mannguyen@gmail.com', 0),
    (2, 'KH002', N'Trần Thị Bình', '0909000087', 'binhtran@gmail.com', 0),
    (3, 'KH003', N'Lê Văn Lương', '0909000098', 'lươngle@gmail.com', 0),
    (4, 'KH004', N'Phạm Thị Thu', '0909000087', 'thuthi@gmail.com', 0),
    (5, 'KH005', N'Hoàng Văn Đạo', '0909000065', 'jsd@gmail.com', 0),
    (6, 'KH006', N'Phùng Thuỳ Linh', '0909007665', 'hshjse@gmail.com', 0),
    (7, 'KH009', N'Phùng Tiến Lâm', '090909865', 'rtdg@gmail.com', 0),
    (8, 'KH010', N'Nguyễn Văn Huệ', '0987006565', 'huevan@gmail.com', 0);
SET IDENTITY_INSERT Customer OFF;

-- === ADDRESS SHIPPING ===
SET IDENTITY_INSERT AddressShipping ON;
INSERT INTO AddressShipping (id, address, customer_id)
VALUES
    -- Địa chỉ cho KH001
    (1, N'12 Đường Láng, Hà Nội', 1),
    (2, N'123 Lê Lợi, Hà Nội', 1),
    (3, N'456 Kim Mã, Hà Nội', 1),
    -- Địa chỉ cho KH002
    (4, N'46 CMT8, TP.HCM', 2),
    (5, N'91 Lý Thường Kiệt, TP.HCM', 2),
    -- Địa chỉ cho KH003
    (6, N'89 Trường Chinh, Đà Nẵng', 3),
    -- Địa chỉ cho KH004
    (7, N'01 Nguyễn Huệ, Cần Thơ', 4),
    (8, N'10 Hai Bà Trưng, Cần Thơ', 4),
    -- Địa chỉ cho KH005
    (9, N'17 Phan Đình Phùng, Huế', 5);
SET IDENTITY_INSERT AddressShipping OFF;

-- === ACCOUNT ===
-- === ACCOUNT ===
-- KHÔNG cần SET IDENTITY_INSERT Account ON
INSERT INTO Account (code, birthDay, email, password, createDate, updateDate, isNonLocked, customer_id, roleId)
VALUES
    ('TK001', '1999-01-01', 'hieule@gmail.com', 'pass123', GETDATE(), GETDATE(), 1, 1, 1),
    ('TK026', '2000-05-10', 'thuylinhh@gmail.com', 'pass456', GETDATE(), GETDATE(), 1, 6, 2),
    ('TK003', '1995-03-15', 'anhhieu@gmail.com', 'pass789', GETDATE(), GETDATE(), 1, 3, 3),
    ('TK019', '1998-07-20', 'ducquang@gmail.com', 'passabc', GETDATE(), GETDATE(), 1, 4, 1),
    ('TK025', '2002-11-12', 'vanhai@gmail.com', 'passxyz', GETDATE(), GETDATE(), 1, 5, 2),
    ('TK030', '2002-11-12', 'hieugiang@gmail.com', 'passxyz', GETDATE(), GETDATE(), 1, 7, 2),
    ('TK031', '2002-11-12', 'trungkien@gmail.com', 'passfz', GETDATE(), GETDATE(), 1, 8, 2);

