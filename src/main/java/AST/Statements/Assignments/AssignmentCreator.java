package AST.Statements.Assignments;

import java.util.Random;

@FunctionalInterface
public interface AssignmentCreator<T> {

    Assignment<T> apply(Random random, boolean printAssignment);

}
