package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An optional, immutable remark about a person. An empty value means no remark.
 */
public class Remark {

    public final String value;

    /**
     * Creates a remark; an empty value represents no remark.
     */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }
        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
