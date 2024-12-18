package ligma.visitor;

import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;

public class ProgramVisitor extends LigmaBaseVisitor<Object> {

    @Override
    public Object visitProgram(LigmaParser.ProgramContext ctx) {
        return super.visitProgram(ctx);
    }

}
