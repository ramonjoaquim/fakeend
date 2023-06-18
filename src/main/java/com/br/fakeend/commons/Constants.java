package com.br.fakeend.commons;

import lombok.Getter;

@Getter
public class Constants {

  private Constants() {
  }

  public static final String PATH = "path";
  public static final String NAME = "name";
  public static final String REQUEST_METHOD_TYPE = "requestMethodType";
  public static final String ID = "id";
  public static final String BODY = "body";
  public static final String ID_PATH = "idPath";
  public static final Integer ID_PATH_DEFAULT = -1;
  public static final String PURGE_ALL = "purge-all";
}
