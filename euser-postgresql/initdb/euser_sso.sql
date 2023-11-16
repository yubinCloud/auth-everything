--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0 (Debian 16.0-1.pgdg110+1)
-- Dumped by pg_dump version 16.0 (Debian 16.0-1.pgdg110+1)


CREATE DATABASE euser_sso;

\c euser_sso;

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: avue_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.avue_role (
    role_id integer NOT NULL,
    permissions jsonb DEFAULT '[]'::jsonb NOT NULL,
    name character varying(30)
);


ALTER TABLE public.avue_role OWNER TO postgres;

--
-- Name: TABLE avue_role; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.avue_role IS 'avue 的角色表信息';


--
-- Name: COLUMN avue_role.role_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.avue_role.role_id IS '角色 ID';


--
-- Name: COLUMN avue_role.permissions; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.avue_role.permissions IS '角色对应的权限信息';


--
-- Name: COLUMN avue_role.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.avue_role.name IS '角色名';


--
-- Name: avue_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.avue_role ALTER COLUMN role_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.avue_role_role_id_seq
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1
);


--
-- Name: euser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.euser (
    username character varying(15) NOT NULL,
    password character varying(255) NOT NULL,
    created_by character varying(20) NOT NULL,
    checked boolean DEFAULT false NOT NULL,
    screen_name character varying(15) DEFAULT ''::character varying NOT NULL,
    note text,
    create_time bigint NOT NULL,
    uid integer NOT NULL,
    avue_roles jsonb DEFAULT '[]'::jsonb NOT NULL,
    publicapi_ids jsonb DEFAULT '[]'::jsonb NOT NULL,
    labels jsonb DEFAULT '{}'::jsonb NOT NULL,
    last_updated_iuser character varying(20),
    last_updated_time bigint
);


ALTER TABLE public.euser OWNER TO postgres;

--
-- Name: TABLE euser; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.euser IS '外部用户表';


--
-- Name: COLUMN euser.username; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.username IS '用户名';


--
-- Name: COLUMN euser.password; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.password IS '密码';


--
-- Name: COLUMN euser.created_by; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.created_by IS '被哪个内部用户创建的，用户的 ID';


--
-- Name: COLUMN euser.checked; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.checked IS '表示该外部用户是否能够使用';


--
-- Name: COLUMN euser.note; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.note IS '备注';


--
-- Name: COLUMN euser.create_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.create_time IS '创建时间';


--
-- Name: COLUMN euser.avue_roles; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.avue_roles IS 'avue 的角色列表';


--
-- Name: COLUMN euser.publicapi_ids; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.publicapi_ids IS '可访问的公开 API 的 ID';


--
-- Name: COLUMN euser.labels; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.labels IS '用户标签';


--
-- Name: COLUMN euser.last_updated_iuser; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.last_updated_iuser IS '最近被哪个内部用户更新的';


--
-- Name: COLUMN euser.last_updated_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.euser.last_updated_time IS '最近被更新的时间';


--
-- Name: euser_uid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.euser_uid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.euser_uid_seq OWNER TO postgres;

--
-- Name: euser_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.euser_uid_seq OWNED BY public.euser.uid;


--
-- Name: publicapi_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.publicapi_role (
    role_id integer NOT NULL,
    access_list jsonb DEFAULT '[]'::jsonb NOT NULL
);


ALTER TABLE public.publicapi_role OWNER TO postgres;

--
-- Name: TABLE publicapi_role; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.publicapi_role IS '公开 API 的角色表';


--
-- Name: COLUMN publicapi_role.role_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.publicapi_role.role_id IS '角色 ID';


--
-- Name: COLUMN publicapi_role.access_list; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.publicapi_role.access_list IS '可以访问的 API 列表';


--
-- Name: publicapi_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.publicapi_role_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.publicapi_role_role_id_seq OWNER TO postgres;

--
-- Name: publicapi_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.publicapi_role_role_id_seq OWNED BY public.publicapi_role.role_id;


--
-- Name: euser uid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.euser ALTER COLUMN uid SET DEFAULT nextval('public.euser_uid_seq'::regclass);


--
-- Name: publicapi_role role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicapi_role ALTER COLUMN role_id SET DEFAULT nextval('public.publicapi_role_role_id_seq'::regclass);


--
-- Data for Name: avue_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.avue_role (role_id, permissions, name) FROM stdin;
0	[{"visualId": "1", "whitelist": "13a3258b-9067-4c6b-974d-4d9dfcb0347b"}]	test-role
\.


--
-- Data for Name: euser; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.euser (username, password, created_by, checked, screen_name, note, create_time, uid, avue_roles, publicapi_ids, labels, last_updated_iuser, last_updated_time) FROM stdin;
inet	b6b94ebacd050b469e0d64596df6a08d453ba2c6d01a6cc2bfb8d7ecde9c58f1	admin	t	INET		1699596721	9	[0, 1]	[]	{"access-avue": true}	\N	\N
hit	b6b94ebacd050b469e0d64596df6a08d453ba2c6d01a6cc2bfb8d7ecde9c58f1	admin	f	HIT2	哈尔滨工业大学	1699339435	7	[0]	[]	{"access-avue": true}	admin	1699606900
\.


--
-- Data for Name: publicapi_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.publicapi_role (role_id, access_list) FROM stdin;
\.


--
-- Name: avue_role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.avue_role_role_id_seq', 0, true);


--
-- Name: euser_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.euser_uid_seq', 9, true);


--
-- Name: publicapi_role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.publicapi_role_role_id_seq', 1, false);


--
-- Name: euser euser_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.euser
    ADD CONSTRAINT euser_pk PRIMARY KEY (uid);


--
-- Name: euser unique_username; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.euser
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- Name: CONSTRAINT unique_username ON euser; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON CONSTRAINT unique_username ON public.euser IS '用户名唯一';


--
-- PostgreSQL database dump complete
--

