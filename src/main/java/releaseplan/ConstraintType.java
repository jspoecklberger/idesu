package releaseplan;

public enum ConstraintType {
    EQUAL, NOT_EQUAL, OR, AT_LEAST_ONE, AT_LEASTONE_A, EXCLUDES, AT_MOST_ONE_A,
    LOWER_THAN, LOWER_EQUAL_THAN, GREATER_THAN, GREATER_EQUAL_THAN, VALUE_DEPENDENCY,
    FIXED, NO_LATER_THAN, NOT_EARLIER_THAN, CAPACITY
}
