
package net.specialattack.modjam.scoreboard;

import java.util.List;

import net.minecraft.scoreboard.ScoreObjectiveCriteria;

public class ScoreTDCriteria implements ScoreObjectiveCriteria {

    private final String identifier;

    @SuppressWarnings("unchecked")
    public ScoreTDCriteria(String par1Str) {
        this.identifier = par1Str;
        ScoreObjectiveCriteria.field_96643_a.put(par1Str, this);
    }

    @Override
    public String func_96636_a() {
        return this.identifier;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int func_96635_a(List par1List) {
        return 0;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
