 package game;

    public class Player {
        char id;
        String name;
        Bead bead;
        int walls;
        boolean skip_level;
        public char getId() { return this.id; }
        public String getName() { return this.name; }
        public Bead getBead() { return this.bead; }
        public int getWalls() { return this.walls; }
        public boolean isSkip_level() { return this.skip_level; }
        public void setBead(int y, int x) { this.bead = new Bead(y, x); }
        public void decreaseWalls() { this.walls--; }
        public void increaseWalls() { this.walls++; }
        public void setId(char id) {
            this.id = id;
            if (id == 'U')
                this.bead = new Bead(0, 8);
            else
                this.bead = new Bead(16, 8);
        }

        public Player(String name, char id, int walls) {
            this.name = name;
            this.walls = walls;
            skip_level = false;
            setId(id);
        }
    }
