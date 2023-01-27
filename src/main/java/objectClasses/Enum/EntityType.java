package objectClasses.Enum;

public enum EntityType {

    character {
        @Override
        public void attack() {
            System.out.println("character attacking");
        }
    },

    skeletonWarrior {
        @Override
        public void attack() {
            System.out.println("skeletonWarrior attacking");
        }
    },

    skeletonArcher {
        @Override
        public void attack() {
            System.out.println("skeletonArcher attacking");
        }
    };

    @Override
    public String
    toString() {
        return super.toString();
    }

    public abstract void attack();
}
