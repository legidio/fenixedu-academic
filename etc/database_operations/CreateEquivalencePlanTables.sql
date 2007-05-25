CREATE TABLE EQUIVALENCE_PLAN (
    ID_INTERNAL int(11) not null auto_increment,
    KEY_ROOT_DOMAIN_OBJECT int(11) not null default 1,
	KEY_DEGREE_CURRICULAR_PLAN int(11) null,
	KEY_STUDENT_CURRICULAR_PLAN int(11) null,
	OJB_CONCRETE_CLASS varchar(255) not null,
    primary key (ID_INTERNAL),
    index (KEY_ROOT_DOMAIN_OBJECT),
	index (KEY_DEGREE_CURRICULAR_PLAN),
	index (KEY_STUDENT_CURRICULAR_PLAN)
) Type=InnoDB;

CREATE TABLE EQUIVALENCE_PLAN_ENTRY (
    ID_INTERNAL int(11) not null auto_increment,
    KEY_ROOT_DOMAIN_OBJECT int(11) not null default 1,
	KEY_EQUIVALENCE_PLAN int(11) not null,
    primary key (ID_INTERNAL),
    index (KEY_ROOT_DOMAIN_OBJECT),
	index (KEY_EQUIVALENCE_PLAN)
) Type=InnoDB;


CREATE TABLE EQUIVALENCE_PLAN_OLD_CURRICULAR_COURSE (
    ID_INTERNAL int(11) not null auto_increment,
    KEY_EQUIVALENCE_PLAN int(11) not null,
	KEY_CURRICULAR_COURSE int(11) not null,
    primary key (ID_INTERNAL),
	index (KEY_EQUIVALENCE_PLAN),
    index (KEY_CURRICULAR_COURSE),
	unique (KEY_EQUIVALENCE_PLAN, KEY_CURRICULAR_COURSE)
) Type=InnoDB;

