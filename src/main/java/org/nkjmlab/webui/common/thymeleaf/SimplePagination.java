package org.nkjmlab.webui.common.thymeleaf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class SimplePagination {

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final String URL_PARAM_PAGE = "page";
	public static final String URL_PARAM_PERPAGE = "max";

	public static final int LEFT_EDGE = 2;
	public static final int RIGHT_EDGE = 2;
	public static final int LEFT_CURRENT = 2;
	public static final int RIGHT_CURRENT = 3;

	private final int page;
	private final int perPage;
	private final int totalCount;

	public SimplePagination(int page, int perPage, int totalCount) {
		this.page = page;
		this.perPage = perPage;
		this.totalCount = totalCount;
	}

	public int getPage() {
		return this.page;
	}

	public int getPerPage() {
		return this.perPage;
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public int getTotalPage() {
		return (int) Math.ceil(this.totalCount / (double) this.perPage);
	}

	public boolean hasPrev() {
		return this.page > 1;
	}

	public boolean hasNext() {
		return this.page < getTotalPage();
	}

	public int getFirstPage() {
		return this.totalCount == 0 ? 0 : ((this.page - 1) * this.perPage) + 1;
	}

	public int getLastPage() {
		int last = this.page * this.perPage;
		return last < this.totalCount ? last : this.totalCount;
	}

	public List<Integer> getPages() {
		return getPages(LEFT_EDGE, LEFT_CURRENT, RIGHT_CURRENT, RIGHT_EDGE);
	}

	public List<Integer> getPages(int leftEdge, int leftCurrent, int rightCurrent, int rightEdge) {
		int last = 0;
		int pages = getTotalPage();
		List<Integer> result = new LinkedList<>();
		for (int i = 1; i <= pages; i++) {
			if ((i <= leftEdge) ||
					((i > (this.page - leftCurrent - 1)) && (i < (this.page + rightCurrent))) ||
					(i > pages - rightEdge)) {
				if (last + 1 != i) {
					result.add(-1);
				}
				result.add(i);
				last = i;
			}
		}
		return result;
	}

	public String getUrlForOtherPage(HttpServletRequest request, int page)
			throws UnsupportedEncodingException {
		return getUrlForOtherPage(request, page, DEFAULT_ENCODING);
	}

	public String getUrlForOtherPage(HttpServletRequest request, int page, String encoding)
			throws UnsupportedEncodingException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = request.getParameterMap();
		StringBuilder builder = new StringBuilder();
		builder.append(request.getRequestURI());
		builder.append("?");
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				if (!StringUtils.equals(URL_PARAM_PAGE, key)) {
					builder.append(URLEncoder.encode(key, encoding));
					builder.append("=");
					builder.append(URLEncoder.encode(value, encoding));
					builder.append("&");
				}
			}
		}
		builder.append(URL_PARAM_PAGE);
		builder.append("=");
		builder.append(page);
		return builder.toString();
	}

}