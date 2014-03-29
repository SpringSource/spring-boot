package sample.data.gemfire.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.util.ObjectUtils;

/**
 * The Gemstone class is an abstract data type modeling a Gemstone, such as a diamond or a ruby.
 * <p/>
 * @author John Blum
 * @see java.io.Serializable
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.Region
 * @since 1.0.0
 */
@Region("Gemstones")
@SuppressWarnings("unused")
public class Gemstone implements Serializable {

  @Id
  private Long id;

  private String name;

  public Gemstone() {
  }

  public Gemstone(final Long id) {
    this.id = id;
  }

  public Gemstone(final Long id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Gemstone)) {
      return false;
    }

    Gemstone that = (Gemstone) obj;

    return ObjectUtils.nullSafeEquals(this.getName(), that.getName());
  }

  @Override
  public int hashCode() {
    int hashValue = 17;
    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getName());
    return hashValue;
  }

  @Override
  public String toString() {
    return String.format("{ @type = %1$s, id = %2$d, name = %3$s }",
      getClass().getName(), getId(), getName());
  }

}
