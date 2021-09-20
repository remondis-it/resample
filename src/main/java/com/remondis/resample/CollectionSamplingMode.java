package com.remondis.resample;

public enum CollectionSamplingMode {

  /**
   * Use setter to set / replace a field with a new collection.
   */
  USE_SETTER_METHODE,
  /**
   * Use getter for a collection field and add a new Instance to it. (For JAXB generated objects).
   */
  USE_GETTER_AND_ADD

}
