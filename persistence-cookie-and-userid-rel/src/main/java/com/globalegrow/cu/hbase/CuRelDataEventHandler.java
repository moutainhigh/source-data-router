package com.globalegrow.cu.hbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.globalegrow.common.hbase.AbstractHbaseDataPersistence;

@Repository
public class CuRelDataEventHandler extends AbstractHbaseDataPersistence {

	private final Logger logger = LoggerFactory.getLogger(getClass());


}
