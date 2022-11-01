package com.charlenes.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SystemService {

	private Locale defaultLocale;
	private List<String> invoiceHeader = defaultHeader();
	private List<String> invoiceFooter = defaultFooter();
	
	{
		defaultLocale = Locale.getDefault();
		Locale.setDefault(new Locale("en", "GB"));
	}
	
	public void resetSystemLocale() {
		Locale.setDefault(defaultLocale);
	}
	
	public void setSystemLocale(String language, String country) {
		Locale.setDefault(new Locale(language, country));
	}
	
	public Locale getSystemLocale() {
		return Locale.getDefault();
	}

	public List<String> defaultHeader() {
		return Arrays.asList("!!! SET HEADER TEXT !!!");
	}
	
	public void setInvoiceHeader(List<String> header) {
		if (header != null && header.size() > 0) {
			this.invoiceHeader = header;
		}
	}

	public List<String> defaultFooter() {
		return Arrays.asList("!!! SET FOOTER TEXT !!!");
	}
	
	public List<String> getInvoiceHeader() {
		return invoiceHeader;
	}

	public void setInvoiceFooter(List<String> footer) {
		if (footer != null && footer.size() > 0) {
			this.invoiceFooter = footer;
		}
	}

	public List<String> getInvoiceFooter() {
		return invoiceFooter;
	}

}
