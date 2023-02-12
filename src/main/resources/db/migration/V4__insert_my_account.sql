INSERT INTO public.tenant (id, email, tenant_id, created_at, update_at, deleted) VALUES ('4591382d-55f2-4528-984d-42734de5c084', 'fedormoore@gmail.com', '57924dc9-addf-4122-a482-8cb332e841bf', '2022-11-22 11:39:14.394002', '2022-11-22 11:39:14.394002', false);
INSERT INTO public.accounts (id, index_b, email, password, last_name, first_name, middle_names, confirmation, confirmation_code, tenant_id, created_at, update_at, deleted) VALUES ('4591382d-55f2-4528-984d-42734de5c084', 1, 'fedormoore@gmail.com', '$2a$10$GP8Fbjdzn0/eb2k6U1IwEOA33YIZiw9W1jlMkCOmHepHlKHCNj7gO', 'Мур', 'Фёдор', null, true, '1ac471f7-2436-4a0d-9741-53d5d7688181', '57924dc9-addf-4122-a482-8cb332e841bf', '2022-11-22 11:39:14.394002', '2022-11-22 11:39:14.394002', false);
INSERT INTO public.index_b (id, index_b, tenant_id, created_at, update_at, deleted) VALUES ('9343c59f-669b-412b-8bf4-6492d96d98c0', 28, '57924dc9-addf-4122-a482-8cb332e841bf', '2022-12-07 12:13:23.964000', '2022-12-09 16:16:26.665000', false);
INSERT INTO public.accounts_setting (id, preamble_statement_report, tenant_id, created_at, update_at, deleted) VALUES ('47e9140e-1cf2-4b08-8002-3dc2f08f45b2', 'Начальнику 555
от Мур Ф.А.
тест', '57924dc9-addf-4122-a482-8cb332e841bf', '2023-02-02 23:25:30.966000', '2023-02-02 23:25:30.966000', false);

ALTER SEQUENCE accounts_ib_seq RESTART WITH 2;