package objectClasses.Enum;

public enum EntityType {

    character {
        @Override
        public void attack() {
            //System.out.println("character attacking");
        }

        @Override
        public int getAttackCollisionFrame() {
            return 4;
        }
    },

    skeletonWarrior {
        @Override
        public void attack() {
            //System.out.println("skeletonWarrior attacking");
        }

        @Override
        public int getAttackCollisionFrame() {
            return 4;
        }
    },

    skeletonArcher {
        @Override
        public void attack() {
            //System.out.println("skeletonArcher attacking");
        }

        @Override
        public int getAttackCollisionFrame() {
            return 10;
        }
    };

    @Override
    public String
    toString() {
        return super.toString();
    }

    public abstract void attack();

    public abstract int getAttackCollisionFrame();
}
