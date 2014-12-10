-- 自动生成 for MYSQL, 时间:2014-12-09 20:08:55
-- 表描述:平台信息表
drop table if exists PUBPLTINF;
create table PUBPLTINF (
   SysId LONGTEXT(6)   not null comment '系统标识号',
   LogNo VARCHAR(14)   not null comment '当前前置流水号',
   BLogNo VARCHAR(12)   not null comment '当前批量流水号',
   ActDat DECIMAL(8,2) default 0  not null comment '会计日期',
   DatEnd VARCHAR(2) default ' '  not null comment '日终状态',
   EleBk VARCHAR(6)   not null comment '电子联行号',
   EleBk1 VARCHAR(6)   not null comment '电子联行号',
   primary key(SysId,ActDat)
)  partition by range (ActDat)
(
    partition P01 values less than('121'),
    partition PMAX values less than(MAXVALUE)
) comment='平台信息表';
create unique index PUBPLTINF_IDX1 on PUBPLTINF(LogNo ASC,ActDat ASC);
create index PUBPLTINF_IDX2 on PUBPLTINF(ActDat DESC);
-- 序列描述：测试001
delete from sequence where name='ABC_SEQUENCE';
insert into sequence values('ABC_SEQUENCE',1001,1);
