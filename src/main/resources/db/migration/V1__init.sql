DO
$$
    BEGIN
        ---------------------------
        -- Create the Schema     --
        ---------------------------
        IF NOT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = 'public') THEN
            CREATE SCHEMA public;
        END IF;


        ---------------------------
        -- Create the Table      --
        ---------------------------
--         TENANT
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'TENANT'
            ) THEN
            CREATE TABLE public.TENANT
            (
                id         UUID               NOT NULL
                                                       DEFAULT gen_random_uuid() PRIMARY KEY,

                email      VARCHAR(50) UNIQUE NOT NULL,

                tenant_id  UUID               NOT NULL,
                created_at TIMESTAMP                   DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP                   DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN            NOT NULL DEFAULT FALSE
            );
        END IF;

--         ACCOUNTS
        create sequence accounts_ib_seq;

        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'ACCOUNTS'
            ) THEN
            CREATE TABLE public.ACCOUNTS
            (
                id                UUID               NOT NULL
                                                              DEFAULT gen_random_uuid() PRIMARY KEY,
                index_b           INTEGER UNIQUE     NOT NULL DEFAULT NEXTVAL('accounts_ib_seq'),

                email             VARCHAR(50) UNIQUE NOT NULL,
                password          VARCHAR(500)       NOT NULL,

                last_name         VARCHAR(255)       NOT NULL,
                first_name        VARCHAR(255)       NOT NULL,
                middle_names      VARCHAR(255),

                confirmation      BOOLEAN            NOT NULL,
                confirmation_code VARCHAR(500),

                tenant_id         UUID               NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at        TIMESTAMP                   DEFAULT current_TIMESTAMP,
                update_at         TIMESTAMP                   DEFAULT current_TIMESTAMP,
                deleted           BOOLEAN                     DEFAULT FALSE
            );
        END IF;

        alter sequence accounts_ib_seq owned by public.accounts.index_b;

--         ACCOUNTS_SETTING

        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'ACCOUNTS_SETTING'
            ) THEN
            CREATE TABLE public.ACCOUNTS_SETTING
            (
                id                        UUID NOT NULL
                                                        DEFAULT gen_random_uuid() PRIMARY KEY,

                preamble_statement_report VARCHAR(500),

                tenant_id                 UUID NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at                TIMESTAMP     DEFAULT current_TIMESTAMP,
                update_at                 TIMESTAMP     DEFAULT current_TIMESTAMP,
                deleted                   BOOLEAN       DEFAULT FALSE
            );
        END IF;

--         SPR_LOCATION
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_LOCATION'
            ) THEN
            CREATE TABLE public.SPR_LOCATION
            (
                id         UUID         NOT NULL
                                                 DEFAULT gen_random_uuid() PRIMARY KEY,

                type       VARCHAR(50)  NOT NULL,
                name       VARCHAR(255) NOT NULL,
                parent_id  UUID REFERENCES public.SPR_LOCATION (id),

                tenant_id  UUID         NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at TIMESTAMP             DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP             DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN               DEFAULT FALSE,

                UNIQUE (type, name, tenant_id)
            );
        END IF;

--         SPR_ORGANIZATION
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_ORGANIZATION'
            ) THEN
            CREATE TABLE SPR_ORGANIZATION
            (
                id         UUID         NOT NULL
                                                 DEFAULT gen_random_uuid() PRIMARY KEY,

                type       VARCHAR(50)  NOT NULL,
                name       VARCHAR(255) NOT NULL,
                parent_id  UUID REFERENCES public.SPR_ORGANIZATION (id),

                tenant_id  UUID         NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at TIMESTAMP             DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP             DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN               DEFAULT FALSE,

                UNIQUE (type, name, tenant_id)
            );
        END IF;

--         SPR_USERS
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_USERS'
            ) THEN
            CREATE TABLE public.SPR_USERS
            (
                id                    UUID         NOT NULL
                                                            DEFAULT gen_random_uuid() PRIMARY KEY,

                email                 VARCHAR(50)  NOT NULL UNIQUE,
                telephone             VARCHAR(50),

                last_name             VARCHAR(255) NOT NULL,
                first_name            VARCHAR(255) NOT NULL,
                middle_names          VARCHAR(255),

                location_id           UUID REFERENCES public.SPR_LOCATION (id),
                organization_id       UUID REFERENCES public.SPR_ORGANIZATION (id),
                organization_function VARCHAR(255),

                tenant_id             UUID         NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at            TIMESTAMP             DEFAULT current_TIMESTAMP,
                update_at             TIMESTAMP             DEFAULT current_TIMESTAMP,
                deleted               BOOLEAN               DEFAULT FALSE
            );
        END IF;

--         SPR_MATERIAL_VALUE_TYPE
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_MATERIAL_VALUE_TYPE'
            ) THEN
            CREATE TABLE SPR_MATERIAL_VALUE_TYPE
            (
                id           UUID                NOT NULL
                                                          DEFAULT gen_random_uuid() PRIMARY KEY,

                name         VARCHAR(255) UNIQUE NOT NULL,
                add_to_other boolean             NOT NULL, -- Возможность добавления в состав других МЦ
                add_other    boolean             NOT NULL, -- Возможность добавления в себя других МЦ

                tenant_id    UUID                NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at   TIMESTAMP                    DEFAULT current_TIMESTAMP,
                update_at    TIMESTAMP                    DEFAULT current_TIMESTAMP,
                deleted      BOOLEAN                      DEFAULT FALSE
            );
        END IF;

--         SPR_MATERIAL_VALUE
        create sequence material_value_ib_seq;

        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_MATERIAL_VALUE'
            ) THEN
            CREATE TABLE SPR_MATERIAL_VALUE
            (
                id                     UUID           NOT NULL
                                                               DEFAULT gen_random_uuid() PRIMARY KEY,
                index_b                INTEGER UNIQUE NOT NULL DEFAULT NEXTVAL('material_value_ib_seq'),

                material_value_type_id UUID           NOT NULL REFERENCES public.SPR_MATERIAL_VALUE_TYPE (id),
                name_in_org            VARCHAR(255),
                name_firm              VARCHAR(255),
                name_model             VARCHAR(255),

                tenant_id              UUID           NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at             TIMESTAMP               DEFAULT current_TIMESTAMP,
                update_at              TIMESTAMP               DEFAULT current_TIMESTAMP,
                deleted                BOOLEAN                 DEFAULT FALSE,
                UNIQUE (material_value_type_id, name_in_org, name_firm, name_model)
            );
        END IF;

        alter sequence material_value_ib_seq owned by public.spr_material_value.index_b;

--         SPR_COUNTERPARTY
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_COUNTERPARTY'
            ) THEN
            CREATE TABLE SPR_COUNTERPARTY
            (
                id         UUID         NOT NULL
                                                 DEFAULT gen_random_uuid() PRIMARY KEY,

                name       VARCHAR(255) NOT NULL,
                inn        VARCHAR(14),
                telephone  VARCHAR(50),
                email      VARCHAR(100),
                contact    VARCHAR(255),

                tenant_id  UUID         NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at TIMESTAMP             DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP             DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN               DEFAULT FALSE,

                UNIQUE (name, tenant_id)
            );
        END IF;

--         SPR_BUDGET_ACCOUNT
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'SPR_BUDGET_ACCOUNT'
            ) THEN
            CREATE TABLE SPR_BUDGET_ACCOUNT
            (
                id         UUID         NOT NULL
                                                 DEFAULT gen_random_uuid() PRIMARY KEY,

                code       VARCHAR(10)  NOT NULL,
                name       VARCHAR(255) NOT NULL,

                tenant_id  UUID         NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at TIMESTAMP             DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP             DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN               DEFAULT FALSE,

                UNIQUE (code, name, tenant_id)
            );
        END IF;

--         REASON
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'REASON'
            ) THEN
            CREATE TABLE REASON
            (
                id              UUID        NOT NULL
                                                     DEFAULT gen_random_uuid() PRIMARY KEY,
                type_record     VARCHAR(50) NOT NULL,
                date            DATE        NOT NULL,
                number          VARCHAR(50) NOT NULL,
                sum             NUMERIC(10, 2),

                counterparty_id UUID REFERENCES public.SPR_COUNTERPARTY (id),
                organization_id UUID REFERENCES public.SPR_ORGANIZATION (id),

                tenant_id       UUID        NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at      TIMESTAMP            DEFAULT current_TIMESTAMP,
                update_at       TIMESTAMP            DEFAULT current_TIMESTAMP,
                deleted         BOOLEAN              DEFAULT FALSE
            );
        END IF;

--         INDEX_B
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'INDEX_B'
            ) THEN
            CREATE TABLE INDEX_B
            (
                id         UUID NOT NULL
                                         DEFAULT gen_random_uuid() PRIMARY KEY,
                index_b    INT  NOT NULL,

                tenant_id  UUID NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at TIMESTAMP     DEFAULT current_TIMESTAMP,
                update_at  TIMESTAMP     DEFAULT current_TIMESTAMP,
                deleted    BOOLEAN       DEFAULT FALSE
            );
        END IF;

--         MATERIAL_VALUE_ORG
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'MATERIAL_VALUE_ORG'
            ) THEN
            CREATE TABLE MATERIAL_VALUE_ORG
            (
                id                UUID           NOT NULL
                                                          DEFAULT gen_random_uuid() PRIMARY KEY,
                barcode           VARCHAR(20)    NOT NULL,
                status            VARCHAR(50),

                organization_id   UUID REFERENCES public.SPR_ORGANIZATION (id),
                material_value_id UUID REFERENCES public.SPR_MATERIAL_VALUE (id),
                sum               NUMERIC(10, 2) NOT NULL,
                inv_number        VARCHAR(50),
                location_id       UUID REFERENCES public.SPR_LOCATION (id),
                user_id           UUID REFERENCES public.SPR_USERS (id),--сотрудник
                budget_account_id UUID REFERENCES public.SPR_BUDGET_ACCOUNT (id),--учетный счет бюджета
                responsible_id    UUID REFERENCES public.SPR_USERS (id),--материально ответственное лицо
                parent_id         UUID REFERENCES public.MATERIAL_VALUE_ORG (id),

                tenant_id         UUID           NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at        TIMESTAMP               DEFAULT current_TIMESTAMP,
                update_at         TIMESTAMP               DEFAULT current_TIMESTAMP,
                deleted           BOOLEAN                 DEFAULT FALSE,
                UNIQUE (barcode, tenant_id)
            );
        END IF;

--         MATERIAL_VALUE_HISTORY
        IF NOT EXISTS(
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'MATERIAL_VALUE_HISTORY'
            ) THEN
            CREATE TABLE MATERIAL_VALUE_HISTORY
            (
                id                    UUID NOT NULL
                                                    DEFAULT gen_random_uuid() PRIMARY KEY,

                material_value_org_id UUID NOT NULL REFERENCES public.MATERIAL_VALUE_ORG (id) ON DELETE CASCADE,
                type                  VARCHAR(50),
                old_value             VARCHAR(50),
                new_value             VARCHAR(50),
                reason                UUID REFERENCES public.REASON (id),
                note                  VARCHAR(500),
                parent_id             UUID REFERENCES public.MATERIAL_VALUE_HISTORY (id),

                tenant_id             UUID NOT NULL DEFAULT current_setting('app.current_tenantId')::UUID,
                created_at            TIMESTAMP     DEFAULT current_TIMESTAMP,
                update_at             TIMESTAMP     DEFAULT current_TIMESTAMP,
                deleted               BOOLEAN       DEFAULT FALSE
            );
        END IF;

        ---------------------------
        -- Enable RLS            --
        ---------------------------
        ALTER TABLE public.ACCOUNTS
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.ACCOUNTS_SETTING
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_LOCATION
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_ORGANIZATION
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_USERS
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_MATERIAL_VALUE_TYPE
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_MATERIAL_VALUE
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_COUNTERPARTY
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.SPR_BUDGET_ACCOUNT
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.REASON
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.INDEX_B
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.MATERIAL_VALUE_ORG
            ENABLE ROW LEVEL SECURITY;

        ALTER TABLE public.MATERIAL_VALUE_HISTORY
            ENABLE ROW LEVEL SECURITY;

        ---------------------------
        -- Create the RLS Policy --
        ---------------------------
        DROP POLICY IF EXISTS account_isolation_policy ON public.ACCOUNTS;

        CREATE POLICY account_isolation_policy ON public.ACCOUNTS
            USING (public.ACCOUNTS.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS account_setting_isolation_policy ON public.ACCOUNTS_SETTING;

        CREATE POLICY account_setting_isolation_policy ON public.ACCOUNTS_SETTING
            USING (public.ACCOUNTS_SETTING.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS location_isolation_policy ON public.SPR_LOCATION;

        CREATE POLICY location_isolation_policy ON public.SPR_LOCATION
            USING (public.SPR_LOCATION.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS organization_isolation_policy ON public.SPR_ORGANIZATION;

        CREATE POLICY organization_isolation_policy ON public.SPR_ORGANIZATION
            USING (public.SPR_ORGANIZATION.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS users_isolation_policy ON public.SPR_USERS;

        CREATE POLICY users_isolation_policy ON public.SPR_USERS
            USING (public.SPR_USERS.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS material_value_type_isolation_policy ON public.SPR_MATERIAL_VALUE_TYPE;

        CREATE POLICY material_value_type_isolation_policy ON public.SPR_MATERIAL_VALUE_TYPE
            USING (public.SPR_MATERIAL_VALUE_TYPE.tenant_id = current_setting('app.current_tenantId')::UUID or
                   public.SPR_MATERIAL_VALUE_TYPE.tenant_id = '57924dc9-addf-4122-a482-8cb332e841bf'::UUID)
            WITH CHECK (public.SPR_MATERIAL_VALUE_TYPE.tenant_id = current_setting('app.current_tenantId')::UUID);


        DROP POLICY IF EXISTS material_value_isolation_policy ON public.SPR_MATERIAL_VALUE;

        CREATE POLICY material_value_isolation_policy ON public.SPR_MATERIAL_VALUE
            USING (public.SPR_MATERIAL_VALUE.tenant_id = current_setting('app.current_tenantId')::UUID or
                   public.SPR_MATERIAL_VALUE.tenant_id = '57924dc9-addf-4122-a482-8cb332e841bf'::UUID)
            WITH CHECK (public.SPR_MATERIAL_VALUE.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS counterparty_isolation_policy ON public.SPR_COUNTERPARTY;

        CREATE POLICY counterparty_isolation_policy ON public.SPR_COUNTERPARTY
            USING (public.SPR_COUNTERPARTY.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS budget_account_isolation_policy ON public.SPR_BUDGET_ACCOUNT;

        CREATE POLICY budget_account_isolation_policy ON public.SPR_BUDGET_ACCOUNT
            USING (public.SPR_BUDGET_ACCOUNT.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS reason_isolation_policy ON public.REASON;

        CREATE POLICY reason_isolation_policy ON public.REASON
            USING (public.REASON.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS index_b_isolation_policy ON public.INDEX_B;

        CREATE POLICY index_b_isolation_policy ON public.INDEX_B
            USING (public.INDEX_B.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS material_value_org_isolation_policy ON public.MATERIAL_VALUE_ORG;

        CREATE POLICY material_value_org_isolation_policy ON public.MATERIAL_VALUE_ORG
            USING (public.MATERIAL_VALUE_ORG.tenant_id = current_setting('app.current_tenantId')::UUID);

        DROP POLICY IF EXISTS material_value_history_isolation_policy ON public.MATERIAL_VALUE_HISTORY;

        CREATE POLICY material_value_history_isolation_policy ON public.MATERIAL_VALUE_HISTORY
            USING (public.MATERIAL_VALUE_HISTORY.tenant_id = current_setting('app.current_tenantId')::UUID);

        ---------------------------
        -- Create the app_user   --
        ---------------------------
--         IF NOT EXISTS(
--                 SELECT
--                 FROM pg_catalog.pg_roles
--                 WHERE rolname = 'app_user') THEN
--             CREATE ROLE app_user LOGIN PASSWORD 'app_user';
--         END IF;

        --------------------------------
        -- Grant Access to the Schema --
        --------------------------------
        GRANT USAGE ON SCHEMA public TO app_user;

        -------------------------------------
        -- Grant Access to public.accounts --
        -------------------------------------
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.TENANT TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.ACCOUNTS TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.ACCOUNTS_SETTING TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_LOCATION TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_ORGANIZATION TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_USERS TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_MATERIAL_VALUE_TYPE TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_MATERIAL_VALUE TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_COUNTERPARTY TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.SPR_BUDGET_ACCOUNT TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.REASON TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.INDEX_B TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.MATERIAL_VALUE_ORG TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.MATERIAL_VALUE_HISTORY TO app_user;

        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.accounts_ib_seq TO app_user;
        GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE public.material_value_ib_seq TO app_user;
    END;
$$;