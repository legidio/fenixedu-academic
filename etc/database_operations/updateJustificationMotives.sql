
alter table `JUSTIFICATION_MOTIVE` add column `PROCESSING_IN_CURRENT_MONTH` tinyint(1) default 0;
update JUSTIFICATION_MOTIVE set PROCESSING_IN_CURRENT_MONTH=1 where JUSTIFICATION_GROUP='UNPAID_LICENCES';