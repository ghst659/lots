package tc.lots;

public class LotTestImpl implements Lot {
    private String name_ = null;
    private int quantity_ = 0;
    public LotTestImpl(String name, int qty) {
        name_ = name;
        quantity_ = qty;
    }
    @Override
    public String name() {
        return name_;
    }
    @Override
    public int quantity() {
        return quantity_;
    }
}
