create table Sources (
	source_id serial primary key,
	source_name varchar(100) unique not null,
	update_date date,
	description text,
	version text
);
create index idx_sources_source_name on Sources(source_name);


create table Words (
	word_id serial primary key,
	primary_reading varchar(255) not null,
	primary_writing varchar(255) not null,
	part_of_speech varchar(255),
	frequency_rank int,
	source_id int references Sources(source_id) on delete set null,
	unique (primary_reading, primary_writing)
);
create index idx_words_primary_reading on Words(primary_reading);
create index idx_words_sources_id on Words(source_id);


create table Kanji (
	kanji_id serial primary key,
	character varchar(5) unique not null,
	stroke_count int,
	radical varchar(50),
	on_reading varchar(255),
	kun_reading varchar(255),
	meaning varchar(255),
	grade_level int check (grade_level between 1 and 10),
	jlpt_level int check(jlpt_level between 1 and 5),
	frequency int
);
create index idx_kanji_character on Kanji(character);


create table Senses (
	sense_id serial primary key,
	word_id int not null references Words(word_id) on delete cascade,
	definition_en text not null,
	definition_vi text,
	gloss varchar(200),
	sense_number int
);
create index idx_senses_word_id on Senses(word_id);

create table Examples (
	example_id serial primary key,
	sense_id int not null references Senses(sense_id) on delete cascade,
	sentence_jp text not null,
	sentence_en text
);
create index idx_examples_sense_id on Examples(sense_id);

create table Tags (
	tag_id serial primary key,
	tag_name varchar(255) unique not null,
	description varchar(255)
);
create index idx_tags_tag_name on Tags(tag_name);

create table Word_Tags (
	word_tag_id serial primary key,
	word_id int not null references Words(word_id) on delete cascade,
	tag_id int not null references Tags(tag_id) on delete cascade,
	unique (word_id, tag_id)
);
create index idx_word_tags_word_id on Word_Tags(word_id);
create index idx_word_tags_tag_id on Word_Tags(tag_id);

create table Cross_references (
	ref_id serial primary key,
	word_id int not null references Words(word_id) on delete cascade,
	related_word_id int not null references Words(word_id) on delete cascade,
	relation_type varchar(50),
	check (word_id != related_word_id)
);
create index idx_cross_references_word_id on Cross_references(word_id);
create index idx_cross_references_related_word_id on Cross_references(related_word_id);


create table if not exists raw_jmdict (
    raw_id serial primary key,
    data jsonb
);