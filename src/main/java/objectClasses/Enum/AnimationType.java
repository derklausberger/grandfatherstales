package objectClasses.Enum;

public enum AnimationType {

    walking {
        @Override
        public void getAnimationType() {
            System.out.println("is walking");
        }
    },

    attacking {
        @Override
        public void getAnimationType() {
            System.out.println("is attacking");
        }
    },

    dying {
        @Override
        public void getAnimationType() {
            System.out.println("is dying");
        }
    };

    @Override
    public String
    toString() {
        return super.toString();
    }

    public abstract void getAnimationType();
}
