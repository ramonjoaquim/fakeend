package com.br.fakeend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Endpoint {
  private final String name;
  private final String path;
}
