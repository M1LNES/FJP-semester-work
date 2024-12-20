package ligma.ast.expression;

import ligma.enums.DataType;

public abstract class Expression {
//    private DataType dataType; TODO: use Expression type
    private int line;

    protected Expression(int line) {
        this.line = line;
    }

}
