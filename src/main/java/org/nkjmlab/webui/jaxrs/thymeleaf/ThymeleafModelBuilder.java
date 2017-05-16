package org.nkjmlab.webui.jaxrs.thymeleaf;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.nkjmlab.webui.service.user.model.UserAccount;

public class ThymeleafModelBuilder {

	private UserAccount userAccount;
	private File directory;
	private String[] extentions;
	private boolean recursive;

	public ThymeleafModelBuilder setUserAccountAndLocale(UserAccount userAccount) {
		this.userAccount = userAccount;
		return this;
	}

	public ThymeleafModelBuilder setFileUpdateDates(File directory, String[] extentions,
			boolean recursive) {
		this.directory = directory;
		this.extentions = extentions;
		this.recursive = recursive;
		return this;
	}

	public ThymeleafModel build() {
		ThymeleafModel model = new ThymeleafModel();
		if (userAccount != null) {
			model.setCurrentUser(userAccount);
			model.setLocale(userAccount.getLocale());
		}

		if (directory != null) {
			Collection<File> files = FileUtils.listFiles(directory, extentions, recursive);
			files
					.forEach(f -> model
							.put(f.getAbsolutePath().replace(directory.getAbsolutePath(), "")
									.replace(".", "_").replace("-", "_")
									.replace(File.separator, "_")
									.replaceFirst("_", ""),
									f.lastModified()));
		}

		return model;
	}

}
