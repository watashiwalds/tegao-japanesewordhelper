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
	frequency_rank int,
	source_id int references Sources(source_id) on delete set null,
	unique (primary_reading, primary_writing)
);
create index idx_words_primary_reading on Words(primary_reading);
create index idx_words_sources_id on Words(source_id);


create table Kanji (
	kanji_id serial primary key,
	character varchar(5) unique not null,
	on_reading varchar(255),
	kun_reading varchar(255),
	meaning varchar(255)
);
create index idx_kanji_character on Kanji(character);


create table Senses (
	sense_id serial primary key,
	word_id int not null references Words(word_id) on delete cascade,
	definition_en text not null,
	sense_number int
);
create index idx_senses_word_id on Senses(word_id);

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



