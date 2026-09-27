--
-- PostgreSQL database dump
--

\restrict fJtDrQo3lB5pBvrGVJyDMPgU41D8pPYwJPBk8bmMMmRVl4pODQ0Wd9wck60B9cT

-- Dumped from database version 14.21 (Homebrew)
-- Dumped by pg_dump version 14.21 (Homebrew)

-- Started on 2026-09-27 11:37:35 WIB

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE jastipapps;
--
-- TOC entry 3923 (class 1262 OID 16384)
-- Name: jastipapps; Type: DATABASE; Schema: -; Owner: rizkybagusimawan
--

CREATE DATABASE jastipapps WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';


ALTER DATABASE jastipapps OWNER TO rizkybagusimawan;

\unrestrict fJtDrQo3lB5pBvrGVJyDMPgU41D8pPYwJPBk8bmMMmRVl4pODQ0Wd9wck60B9cT
\connect jastipapps
\restrict fJtDrQo3lB5pBvrGVJyDMPgU41D8pPYwJPBk8bmMMmRVl4pODQ0Wd9wck60B9cT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 16385)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 3924 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 251 (class 1255 OID 16454)
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: rizkybagusimawan
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_updated_at_column() OWNER TO rizkybagusimawan;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 214 (class 1259 OID 16512)
-- Name: order_messages; Type: TABLE; Schema: public; Owner: rizkybagusimawan
--

CREATE TABLE public.order_messages (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    order_id uuid NOT NULL,
    sender_id uuid NOT NULL,
    sender_role character varying(20) NOT NULL,
    message text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.order_messages OWNER TO rizkybagusimawan;

--
-- TOC entry 213 (class 1259 OID 16482)
-- Name: orders; Type: TABLE; Schema: public; Owner: rizkybagusimawan
--

CREATE TABLE public.orders (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    product_id uuid NOT NULL,
    user_id uuid NOT NULL,
    jumlah integer DEFAULT 1 NOT NULL,
    catatan text,
    harga_satuan numeric(12,2) NOT NULL,
    total_harga numeric(12,2) GENERATED ALWAYS AS (((jumlah)::numeric * harga_satuan)) STORED,
    status character varying(20) DEFAULT 'pending'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    bukti_foto_url character varying(500),
    keterangan_status text,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY ((ARRAY['pending'::character varying, 'diproses'::character varying, 'selesai'::character varying, 'dibatalkan'::character varying])::text[])))
);


ALTER TABLE public.orders OWNER TO rizkybagusimawan;

--
-- TOC entry 211 (class 1259 OID 16437)
-- Name: password_resets; Type: TABLE; Schema: public; Owner: rizkybagusimawan
--

CREATE TABLE public.password_resets (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    token character varying(255) NOT NULL,
    is_used boolean DEFAULT false NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.password_resets OWNER TO rizkybagusimawan;

--
-- TOC entry 212 (class 1259 OID 16457)
-- Name: products; Type: TABLE; Schema: public; Owner: rizkybagusimawan
--

CREATE TABLE public.products (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nama_produk character varying(200) NOT NULL,
    deskripsi text,
    kategori character varying(100),
    foto_url character varying(500),
    sumber character varying(150),
    harga_asli numeric(12,2) DEFAULT 0 NOT NULL,
    biaya_jasa numeric(12,2) DEFAULT 0 NOT NULL,
    harga_total numeric(12,2) GENERATED ALWAYS AS ((harga_asli + biaya_jasa)) STORED,
    status character varying(20) DEFAULT 'tersedia'::character varying NOT NULL,
    kuota integer DEFAULT 0 NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT products_status_check CHECK (((status)::text = ANY ((ARRAY['tersedia'::character varying, 'closed'::character varying, 'sold_out'::character varying])::text[])))
);


ALTER TABLE public.products OWNER TO rizkybagusimawan;

--
-- TOC entry 210 (class 1259 OID 16422)
-- Name: users; Type: TABLE; Schema: public; Owner: rizkybagusimawan
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    full_name character varying(150) NOT NULL,
    phone_number character varying(20),
    is_verified boolean DEFAULT false NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    role character varying(20) DEFAULT 'user'::character varying NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['user'::character varying, 'admin'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO rizkybagusimawan;

--
-- TOC entry 3917 (class 0 OID 16512)
-- Dependencies: 214
-- Data for Name: order_messages; Type: TABLE DATA; Schema: public; Owner: rizkybagusimawan
--

COPY public.order_messages (id, order_id, sender_id, sender_role, message, created_at) FROM stdin;
26ce438f-909c-4652-816e-1c2b7b2a4bb0	28da3bd9-b6cf-41e1-ae0b-9e8be710390f	025fe88d-3335-4e67-b7f0-c185b3c2fb85	user	bisa cancel ?	2026-09-18 06:54:56.219206+07
2635b3cc-91db-4c5e-bf45-3c8f7333602a	28da3bd9-b6cf-41e1-ae0b-9e8be710390f	025fe88d-3335-4e67-b7f0-c185b3c2fb85	admin	tidak bisa	2026-09-18 06:56:33.458837+07
c3fced56-5d03-41c5-851d-c6f2eec80af2	28da3bd9-b6cf-41e1-ae0b-9e8be710390f	d5059c88-e7b9-4efb-ab15-a6a4076bb57f	admin	tidak bisa	2026-09-18 06:58:13.566115+07
\.


--
-- TOC entry 3916 (class 0 OID 16482)
-- Dependencies: 213
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: rizkybagusimawan
--

COPY public.orders (id, product_id, user_id, jumlah, catatan, harga_satuan, status, created_at, updated_at, bukti_foto_url, keterangan_status) FROM stdin;
00644dfd-a51a-450d-8ed5-7d400627923f	d503d7ed-d326-4ccb-9e08-db0181e0c7dd	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		725000.00	diproses	2026-09-15 09:55:04.878731+07	2026-09-15 16:06:47.62835+07	\N	\N
cc2a7889-4d88-4aad-bdea-4dc9ede4b97a	fdedcca2-f6b1-4eb5-b806-7397ecb0e776	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		400000.00	selesai	2026-09-15 09:40:56.104573+07	2026-09-15 16:06:53.636273+07	\N	\N
fddd9a99-2f72-4dc0-aa64-1fef1117ac64	d503d7ed-d326-4ccb-9e08-db0181e0c7dd	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		725000.00	diproses	2026-09-15 09:36:34.614298+07	2026-09-15 16:07:03.638665+07	\N	\N
d27f63b1-7f19-49e4-8659-e74aae77785b	fdedcca2-f6b1-4eb5-b806-7397ecb0e776	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2	Test	400000.00	selesai	2026-09-15 08:10:53.817974+07	2026-09-15 16:07:17.150506+07	\N	\N
4e84adf1-b727-4038-9a84-c10175e3fed7	6e0fbfd3-e451-419c-be1c-3c0a0bffc1b1	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1	ukuran 42	1650000.00	dibatalkan	2026-09-15 08:15:37.696334+07	2026-09-15 16:07:25.158959+07	\N	\N
458a0dc3-24df-4476-8e84-d0309b4ea4be	d503d7ed-d326-4ccb-9e08-db0181e0c7dd	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1	warna kuning	725000.00	diproses	2026-09-15 08:19:38.958333+07	2026-09-15 16:07:31.162515+07	\N	\N
86781180-0bb1-4226-b51d-4fcb9bdd1726	6e0fbfd3-e451-419c-be1c-3c0a0bffc1b1	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		1650000.00	diproses	2026-09-15 08:25:25.497573+07	2026-09-15 16:07:37.165578+07	\N	\N
e315e6b8-edcb-40f7-8bf1-67035ed65d8b	fdedcca2-f6b1-4eb5-b806-7397ecb0e776	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		400000.00	diproses	2026-09-15 09:32:41.631999+07	2026-09-15 16:07:43.671258+07	\N	\N
c7dfc1ff-3604-4960-a90a-67dc6701095c	fdedcca2-f6b1-4eb5-b806-7397ecb0e776	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		400000.00	diproses	2026-09-15 09:20:24.389328+07	2026-09-15 16:07:50.177315+07	\N	\N
4092b1ff-7b00-4cbf-bf58-6bdb6b16698b	d503d7ed-d326-4ccb-9e08-db0181e0c7dd	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		725000.00	selesai	2026-09-15 09:55:27.909491+07	2026-09-16 21:13:06.942355+07	https://asset.kompas.com/crops/4hAMLBaIcWijM41wkpwcR7x2TRY=/0x0:2121x1414/1200x800/data/photo/2021/06/03/60b8f52fd3968.jpg	Barang sudah diterima pemesan
b71f1279-143a-46fb-9b5a-cdfa0c6161a3	89f6759c-8ade-4d75-8479-e08d46f3984f	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		34150000.00	pending	2026-09-16 21:16:22.694018+07	2026-09-16 21:16:22.69403+07	\N	\N
28da3bd9-b6cf-41e1-ae0b-9e8be710390f	6e0fbfd3-e451-419c-be1c-3c0a0bffc1b1	025fe88d-3335-4e67-b7f0-c185b3c2fb85	1		1650000.00	pending	2026-09-16 21:16:42.169005+07	2026-09-16 21:16:42.169013+07	\N	\N
\.


--
-- TOC entry 3914 (class 0 OID 16437)
-- Dependencies: 211
-- Data for Name: password_resets; Type: TABLE DATA; Schema: public; Owner: rizkybagusimawan
--

COPY public.password_resets (id, user_id, token, is_used, expires_at, created_at) FROM stdin;
92893da1-c58b-418c-a542-ff8a1611b9d1	025fe88d-3335-4e67-b7f0-c185b3c2fb85	baa5b4cb-732e-421d-bc70-7ec976a5f443	t	2026-09-13 15:29:59.937799+07	2026-09-13 14:59:59.937825+07
\.


--
-- TOC entry 3915 (class 0 OID 16457)
-- Dependencies: 212
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: rizkybagusimawan
--

COPY public.products (id, nama_produk, deskripsi, kategori, foto_url, sumber, harga_asli, biaya_jasa, status, kuota, created_by, created_at, updated_at) FROM stdin;
a6541b34-d329-4fce-9a6b-3bee555dee62	Iphone 17 Pro	Iphone resmi I Box	Elektronik	https://www.digimap.co.id/cdn/shop/files/0788-APPMG8H4ID-A-1.jpg?v=1759804292	I Box	22000000.00	100000.00	tersedia	10	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-15 17:29:37.389025+07	2026-09-15 17:35:13.275586+07
7905bf32-8006-428e-b664-3f49bc84abf2	Adidas Samba	Adidas Samba Original	Fahsion	https://assets.adidas.com/images/w_500,f_auto,q_auto/4c70105150234ac4b948a8bf01187e0c_faec/Sepatu_Samba_OG_Hitam_B75807_db01_standard.tiff.jpg	Adidas Singapore	1800000.00	100000.00	tersedia	10	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-15 15:42:51.357145+07	2026-09-15 17:35:13.278411+07
89f6759c-8ade-4d75-8479-e08d46f3984f	Iphone Duo	Garansi Resmi I Box	Elektronik	https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-duo-witb-star-white-202609_FMT_WHH?wid=688&hei=744&fmt=jpeg&qlt=90&.v=UXRzMmJCVFBRbmt6ckpmVFpkSGV6eEtyVERMOFdiWWk0VWhrQzkzZjY1eS9yd2xEK3NmZ0hESDl0Ny9qQ0JkVHM2dldETnFhMG5PaUx3dEUvSEQyWjFKM1Q5eVhMMytSb1pTa0R5OWk3VjFWZVVDWDEwR2dwMitGTkRzT00raGY	I Box Singapore	34000000.00	150000.00	tersedia	9	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-15 17:32:34.383805+07	2026-09-16 21:16:22.712773+07
6e0fbfd3-e451-419c-be1c-3c0a0bffc1b1	Sepatu Nike Air Force 1	Original, size 42, dari toko resmi	Fashion	https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTq1RJ7ZE8vA-D-hd5AGK9YyuhNwLqH8IpX5AyjRd3aHVOdBSEGDWnL0KGe&s=10	Nike Store Singapore	1500000.00	150000.00	tersedia	3	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-13 19:26:05.988647+07	2026-09-16 21:16:42.175574+07
2037fbee-a149-45d0-806e-961897c47434	Mac Book Air M4	Mac Book Air M4	Elektronik	http://localhost:8080/uploads/aa0c6aa8-4511-4852-a1b0-119a6dd66d86.jpg	IBOX	15000000.00	250000.00	tersedia	10	d5059c88-e7b9-4efb-ab15-a6a4076bb57f	2026-09-18 19:25:25.748564+07	2026-09-18 19:27:43.301989+07
fdedcca2-f6b1-4eb5-b806-7397ecb0e776	Skincare Set Korea - Cleanser & Toner	Paket skincare Korea original, cocok untuk kulit sensitif. Isi: facial cleanser 150ml, toner 200ml	Kosmetik	http://10.0.2.2:8080/uploads/ed029be0-5355-42c9-be45-0959ef84fdd1.jpg	Olive Young Korea	350000.00	50000.00	tersedia	10	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-13 20:20:13.53215+07	2026-09-19 11:50:54.318861+07
a1603183-57b5-4d3f-be08-7abe98426acb	test	test	test	http://10.0.2.2:8080/uploads/830b79be-5230-4249-8b7f-422ff6cee07f.jpg	test	1.00	1.00	tersedia	1	d5059c88-e7b9-4efb-ab15-a6a4076bb57f	2026-09-19 11:51:11.778814+07	2026-09-19 11:51:11.778822+07
d503d7ed-d326-4ccb-9e08-db0181e0c7dd	iPhone Case MagSafe Original	Case iPhone original Apple dengan MagSafe, garansi resmi Apple Store	Elektronik	https://down-id.img.susercontent.com/file/id-11134207-81ztn-mf9k3zztk9vs69	Apple Store Singapore	650000.00	75000.00	tersedia	10	025fe88d-3335-4e67-b7f0-c185b3c2fb85	2026-09-13 20:20:24.332914+07	2026-09-15 17:23:30.207045+07
\.


--
-- TOC entry 3913 (class 0 OID 16422)
-- Dependencies: 210
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: rizkybagusimawan
--

COPY public.users (id, email, password_hash, full_name, phone_number, is_verified, is_active, created_at, updated_at, role) FROM stdin;
025fe88d-3335-4e67-b7f0-c185b3c2fb85	test@example.com	$2a$10$QaMavgS0xvhy7KjZVjxAoevfu5KIemOT0ks9XlJcFsZesERzrEXU2	Rizky Bagus Imawan	081278060500	f	t	2026-09-13 14:56:50.016863+07	2026-09-18 06:57:51.313084+07	user
d5059c88-e7b9-4efb-ab15-a6a4076bb57f	rbi@email.com	$2a$10$wwezAUT2jvLtc1I9z8bLuuzF/ORr2dRxg4ffKJhKGUEIP6UNUKySe	rizky	081278060500	f	t	2026-09-18 06:57:20.98631+07	2026-09-18 06:57:51.315126+07	admin
\.


--
-- TOC entry 3764 (class 2606 OID 16520)
-- Name: order_messages order_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.order_messages
    ADD CONSTRAINT order_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 3761 (class 2606 OID 16495)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3749 (class 2606 OID 16444)
-- Name: password_resets password_resets_pkey; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_pkey PRIMARY KEY (id);


--
-- TOC entry 3751 (class 2606 OID 16446)
-- Name: password_resets password_resets_token_key; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_token_key UNIQUE (token);


--
-- TOC entry 3756 (class 2606 OID 16472)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 3743 (class 2606 OID 16435)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3745 (class 2606 OID 16433)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3762 (class 1259 OID 16531)
-- Name: idx_order_messages_order_id; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_order_messages_order_id ON public.order_messages USING btree (order_id);


--
-- TOC entry 3757 (class 1259 OID 16507)
-- Name: idx_orders_product_id; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_orders_product_id ON public.orders USING btree (product_id);


--
-- TOC entry 3758 (class 1259 OID 16508)
-- Name: idx_orders_status; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_orders_status ON public.orders USING btree (status);


--
-- TOC entry 3759 (class 1259 OID 16506)
-- Name: idx_orders_user_id; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_orders_user_id ON public.orders USING btree (user_id);


--
-- TOC entry 3746 (class 1259 OID 16452)
-- Name: idx_password_resets_token; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_password_resets_token ON public.password_resets USING btree (token);


--
-- TOC entry 3747 (class 1259 OID 16453)
-- Name: idx_password_resets_user_id; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_password_resets_user_id ON public.password_resets USING btree (user_id);


--
-- TOC entry 3752 (class 1259 OID 16480)
-- Name: idx_products_created_by; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_products_created_by ON public.products USING btree (created_by);


--
-- TOC entry 3753 (class 1259 OID 16479)
-- Name: idx_products_kategori; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_products_kategori ON public.products USING btree (kategori);


--
-- TOC entry 3754 (class 1259 OID 16478)
-- Name: idx_products_status; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_products_status ON public.products USING btree (status);


--
-- TOC entry 3741 (class 1259 OID 16436)
-- Name: idx_users_email; Type: INDEX; Schema: public; Owner: rizkybagusimawan
--

CREATE INDEX idx_users_email ON public.users USING btree (email);


--
-- TOC entry 3773 (class 2620 OID 16509)
-- Name: orders trg_orders_updated_at; Type: TRIGGER; Schema: public; Owner: rizkybagusimawan
--

CREATE TRIGGER trg_orders_updated_at BEFORE UPDATE ON public.orders FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3772 (class 2620 OID 16481)
-- Name: products trg_products_updated_at; Type: TRIGGER; Schema: public; Owner: rizkybagusimawan
--

CREATE TRIGGER trg_products_updated_at BEFORE UPDATE ON public.products FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3771 (class 2620 OID 16456)
-- Name: users trg_users_updated_at; Type: TRIGGER; Schema: public; Owner: rizkybagusimawan
--

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 3769 (class 2606 OID 16521)
-- Name: order_messages order_messages_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.order_messages
    ADD CONSTRAINT order_messages_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3770 (class 2606 OID 16526)
-- Name: order_messages order_messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.order_messages
    ADD CONSTRAINT order_messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3767 (class 2606 OID 16496)
-- Name: orders orders_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;


--
-- TOC entry 3768 (class 2606 OID 16501)
-- Name: orders orders_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3765 (class 2606 OID 16447)
-- Name: password_resets password_resets_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3766 (class 2606 OID 16473)
-- Name: products products_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: rizkybagusimawan
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE CASCADE;


-- Completed on 2026-09-27 11:37:35 WIB

--
-- PostgreSQL database dump complete
--

\unrestrict fJtDrQo3lB5pBvrGVJyDMPgU41D8pPYwJPBk8bmMMmRVl4pODQ0Wd9wck60B9cT

