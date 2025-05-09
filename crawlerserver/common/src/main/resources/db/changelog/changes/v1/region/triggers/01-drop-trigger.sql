--liquibase formatted sql
--changeset jungwoo_shin:drop-triggers runOnChange="true" dbms:mariadb

DROP TRIGGER IF EXISTS after_region_insert;
DROP TRIGGER IF EXISTS after_region_update;