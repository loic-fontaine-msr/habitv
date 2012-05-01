package com.dabi.habitv.process.category;

public interface ProcessCategoryListener {

	void getProviderCategories(String providerName);

	void categoriesSaved(String grabconfigXmlFile);

}
