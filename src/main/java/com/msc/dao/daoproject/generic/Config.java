package com.msc.dao.daoproject.generic;

/**
 *
 * @author Micky
 */
public class Config extends DAO {

    public static String getTemplateUploadFolder() {
        return config.getProperty("ws.upload.template.folder");
    }

}
