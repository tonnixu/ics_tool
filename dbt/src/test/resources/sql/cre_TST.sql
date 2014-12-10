-- �Զ����� for MYSQL, ʱ��:2014-12-09 20:08:55
-- ������:ƽ̨��Ϣ��
drop table if exists PUBPLTINF;
create table PUBPLTINF (
   SysId LONGTEXT(6)   not null comment 'ϵͳ��ʶ��',
   LogNo VARCHAR(14)   not null comment '��ǰǰ����ˮ��',
   BLogNo VARCHAR(12)   not null comment '��ǰ������ˮ��',
   ActDat DECIMAL(8,2) default 0  not null comment '�������',
   DatEnd VARCHAR(2) default ' '  not null comment '����״̬',
   EleBk VARCHAR(6)   not null comment '�������к�',
   EleBk1 VARCHAR(6)   not null comment '�������к�',
   primary key(SysId,ActDat)
)  partition by range (ActDat)
(
    partition P01 values less than('121'),
    partition PMAX values less than(MAXVALUE)
) comment='ƽ̨��Ϣ��';
create unique index PUBPLTINF_IDX1 on PUBPLTINF(LogNo ASC,ActDat ASC);
create index PUBPLTINF_IDX2 on PUBPLTINF(ActDat DESC);
-- ��������������001
delete from sequence where name='ABC_SEQUENCE';
insert into sequence values('ABC_SEQUENCE',1001,1);
