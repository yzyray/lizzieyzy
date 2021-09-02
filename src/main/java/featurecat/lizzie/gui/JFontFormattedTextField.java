package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

public class JFontFormattedTextField extends JFormattedTextField {
  public JFontFormattedTextField() {
    super();
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontFormattedTextField(String text) {
    super();
    this.setText(text);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontFormattedTextField(java.text.Format format) {
    super();
    setFormatterFactory(getDefaultFormatterFactory(format));
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  private AbstractFormatterFactory getDefaultFormatterFactory(Object type) {
    if (type instanceof DateFormat) {
      return new DefaultFormatterFactory(new DateFormatter((DateFormat) type));
    }
    if (type instanceof NumberFormat) {
      return new DefaultFormatterFactory(new NumberFormatter((NumberFormat) type));
    }
    if (type instanceof Format) {
      return new DefaultFormatterFactory(new InternationalFormatter((Format) type));
    }
    if (type instanceof Date) {
      return new DefaultFormatterFactory(new DateFormatter());
    }
    if (type instanceof Number) {
      AbstractFormatter displayFormatter = new NumberFormatter();
      ((NumberFormatter) displayFormatter).setValueClass(type.getClass());
      AbstractFormatter editFormatter = new NumberFormatter(new DecimalFormat("#.#"));
      ((NumberFormatter) editFormatter).setValueClass(type.getClass());

      return new DefaultFormatterFactory(displayFormatter, displayFormatter, editFormatter);
    }
    return new DefaultFormatterFactory(new DefaultFormatter());
  }
}
