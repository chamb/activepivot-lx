/*
 * (C) Quartet FS 2017
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.quartetfs.fwk.format.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.qfs.logging.MessagesComposer;
import com.quartetfs.fwk.format.IPatternFormatter;
import com.quartetfs.fwk.types.ITime;
import com.quartetfs.fwk.util.MessageUtil;

/**
 * <p>
 * Use method <code>format(Object)</code> to format a date object with the wanted pattern. To
 * achieve this, an underlying {@link DateTimeFormatter} is used.
 *
 * <p>
 * For example : Format a LocalDate value on a specific level "tradeDate" with the pattern
 * "yyyyMMdd", the formatted value will be displayed like "20061225".
 *
 * @author Quartet Financial Systems
 *
 */
public class DateFormatter implements IPatternFormatter, Serializable {
	/** The serialVersionUID */
	private static final long serialVersionUID = 8729457941318641483L;

	/** Logger */
	protected static final Logger LOGGER = Logger.getLogger(MessagesComposer.LOGGER_NAME);

	/** Extended plugin value type. For example "DATE" for plugin key "DATE[yyyy-MM-dd]" */
	public static final String TYPE = "DATE";

	/**
	 * Default pattern. See {@link DateTimeFormatterBuilder} and {@link DateTimeFormatter } for
	 * pattern specifications.
	 */
	public static final String DEFAULT_PATTERN = "[yyyy-MM-dd]['T'HH:mm:ss][xxx]";

	/** The locale used for formatting. See {@link Locale} and {@link DateTimeFormatter#ofPattern(String, Locale)}. */
	protected final Locale locale;

	/** The pattern used to build {@link #formatter} */
	protected final String pattern;

	/** The timezone to format to. See {@link ZoneId} and {@link DateTimeFormatter#withZone(ZoneId)}.*/
	protected final ZoneId zoneId;

	/** Delegate {@link DateTimeFormatter} */
	protected transient DateTimeFormatter formatter;

	/**
	 * Default constructor. The locale and timezone are the system default, and the pattern is
	 * {@link #DEFAULT_PATTERN}.
	 *
	 */
	public DateFormatter() {
		this(Locale.getDefault(Category.FORMAT), DEFAULT_PATTERN, ZoneId.systemDefault());
	}

	/**
	 * Constructor. The locale and timezone are the system default.
	 *
	 * @param pattern the pattern used for formatting
	 */
	public DateFormatter(String pattern) {
		this(Locale.getDefault(Category.FORMAT), pattern, ZoneId.systemDefault());
	}

	/**
	 * Constructor. The default pattern used is {@link #DEFAULT_PATTERN}
	 *
	 * @param locale the locale used for formatting
	 * @param zoneId the id of the output timezone
	 */
	public DateFormatter(Locale locale, ZoneId zoneId) {
		this(locale, DEFAULT_PATTERN, zoneId);
	}

	/**
	 * Constructor
	 *
	 * @param locale the locale used for formatting
	 * @param pattern the pattern used for formatting
	 * @param zoneId the output timezone
	 */
	public DateFormatter(Locale locale, String pattern, ZoneId zoneId) {
		this.locale = locale;
		this.pattern = pattern;
		this.zoneId = zoneId;
		this.formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(zoneId);
	}

	/**
	 * @return the extended plugin value type
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	/**
	 * See {@link Locale} and {@link DateTimeFormatter#ofPattern(String, Locale)}.
	 *
	 * @return the {@link Locale} used for formatting.
	 */
	public Locale getLocale() {
		return locale;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	/**
	 * See {@link ZoneId} and {@link DateTimeFormatter#withZone(ZoneId)}.
	 *
	 * @return the {@link ZoneId} representing the timezone used for formattting.
	 */
	public ZoneId getZoneId() {
		return zoneId;
	}

	/**
	 * @param value the date value to be formatted.
	 * @return formatted string
	 */
	@Override
	public String format(Object value) {
		if (value == null) {
			return formatNull();
		} else {
			return formatValue(value);
		}
	}

	/**
	 * Formats a null value.
	 *
	 * @return the result of formatting a null value
	 */
	protected String formatNull() {
		return "";
	}

	/**
	 * @param value the date value to be formatter
	 * @return the formatter String
	 */
	public String formatValue(Object value) {
		if (value instanceof TemporalAccessor) {
			return formatter.format((TemporalAccessor) value);
		} else if (value instanceof Date) {
			final Instant instant = Instant.ofEpochMilli(((Date) value).getTime());
			return formatter.format(instant);
		} else if (value instanceof ITime) {
			ITime quartetTime = (ITime) value;
			return formatter.format(Instant.ofEpochMilli(quartetTime.getTime()));
		} else if (value instanceof Long) {
			final Instant instant = Instant.ofEpochMilli((Long) value);
			return formatter.format(instant);
		}

		if (LOGGER.isLoggable(Level.FINEST)) {
			if (value != null) {
				LOGGER.log(
						Level.FINEST,
						MessageUtil.formMessage(
								MessagesComposer.EXC_NOT_EXPECTED_DATE,
								value.getClass().getSimpleName(),
								value));
			} else {
				LOGGER.log(
						Level.FINEST,
						MessageUtil.formMessage(
								MessagesComposer.EXC_NOT_EXPECTED_DATE,
								null,
								null));
			}
		}

		return String.valueOf(value);
	}

	@Override
	public String toString() {
		return "DateFormatter[locale=" + locale + ", pattern=" + pattern + ", zoneId=" + zoneId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((zoneId == null) ? 0 : zoneId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DateFormatter other = (DateFormatter) obj;
		if (locale == null) {
			if (other.locale != null) {
				return false;
			}
		} else if (!locale.equals(other.locale)) {
			return false;
		}
		if (pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!pattern.equals(other.pattern)) {
			return false;
		}
		if (zoneId == null) {
			if (other.zoneId != null) {
				return false;
			}
		} else if (!zoneId.equals(other.zoneId)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * DESERIALIZATION Since we do not serialize the underlying formatter, this method is needed to
	 * reconstruct it at deserialization time. See {@link Serializable}.
	 *
	 * @param in the input stream to deserialize
	 *
	 * @throws IOException if something wrong occurs when reading the stream
	 * @throws ClassNotFoundException if something wrong occurs when casting the retrieved objects
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(zoneId);
	}

}
