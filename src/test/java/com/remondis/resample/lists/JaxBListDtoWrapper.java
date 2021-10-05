package com.remondis.resample.lists;

/**
 * JAXB DTO having no setters for lists.
 */
class JaxBListDtoWrapper {

  private JaxBListDto jaxBListDto;

  public JaxBListDtoWrapper() {
    super();
  }

  public JaxBListDto getJaxBListDto() {
    return jaxBListDto;
  }

  public void setJaxBListDto(JaxBListDto jaxBListDto) {
    this.jaxBListDto = jaxBListDto;
  }

  @Override
  public String toString() {

    return "JaxBListDtoWrapper{ " + jaxBListDto.toString() + " }";
  }

}
