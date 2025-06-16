package org.example.dungeonCrawler.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Map {
    private final Room[][] rooms;
    private final int width;
    private final int height;
    private final Random random;
    private final List<Point> roomCenters;
    private Point startPosition;
    private Point bossPosition;
    private int numberOfRooms;

    private static final int MIN_ROOM_SIZE = 2;
    private static final int MAX_ROOM_SIZE = 3;
    private static final int ROOM_PADDING = 2;
    private static final double ENEMY_SPAWN_CHANCE_IN_ROOM = 0.10;
    private static final double ITEM_SPAWN_CHANCE_IF_NO_ENEMY = 0.12;
    private static final double ITEM_SPAWN_CHANCE_IF_ENEMY_BLOCKED = 0.20;

    public static class Point {
        public int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.rooms = new Room[height][width];
        this.random = new Random();
        this.roomCenters = new ArrayList<>();

        if (width > 0 && height > 0) {
            generateDungeon();
        } else {
            this.startPosition = new Point(0,0);
        }
    }

    private void generateDungeon() {
        fillWithWalls();
        generateRoomLabyrinth();

        if (roomCenters.isEmpty() && this.width > 0 && this.height > 0) {
            createFallbackRoom();
        }

        connectRooms();
        placeSpecialRooms();
        addTreasures();
        addEventRooms();
        addMerchant();
        populateRoomsWithEnemiesAndItems();

        if (this.startPosition == null) {
            ensureValidStartPosition();
        }
    }

    private void generateRoomLabyrinth() {
        int baseRoomsEstimate = getBaseRoomsEstimate();
        int variation = Math.max(5, baseRoomsEstimate / 3);
        this.numberOfRooms = baseRoomsEstimate + random.nextInt(variation + 1);
        roomCenters.clear();

        generateRoomsWithDenseGridStrategy();
        generateRoomsWithRandomStrategy();
    }

    private int getBaseRoomsEstimate() {
        int mapArea = this.width * this.height;
        int avgRoomFootprint = ((MAX_ROOM_SIZE + MIN_ROOM_SIZE) / 2 + 2 * ROOM_PADDING);
        int baseRoomsEstimate = mapArea / (avgRoomFootprint * avgRoomFootprint);
        baseRoomsEstimate = Math.max(10, baseRoomsEstimate);
        if (width > 15 && height > 15) {
            baseRoomsEstimate = Math.max(15, mapArea / ((MAX_ROOM_SIZE + ROOM_PADDING*2) * (MAX_ROOM_SIZE + ROOM_PADDING*2) / 2 ));
        }
        return baseRoomsEstimate;
    }

    private void generateRoomsWithDenseGridStrategy() {
        int step = MAX_ROOM_SIZE + 2 * ROOM_PADDING + 1;

        for (int y = ROOM_PADDING; y < height - MAX_ROOM_SIZE - ROOM_PADDING; y += step) {
            for (int x = ROOM_PADDING; x < width - MAX_ROOM_SIZE - ROOM_PADDING; x += step) {
                if (roomCenters.size() >= numberOfRooms) break;
                int roomW = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
                int roomH = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
                if (x + roomW + ROOM_PADDING > width || y + roomH + ROOM_PADDING > height) continue;
                if (canPlaceRoomAtWithPadding(x, y, roomW, roomH)) {
                    createRoom(x, y, roomW, roomH);
                    roomCenters.add(new Point(x + roomW / 2, y + roomH / 2));
                }
            }
            if (roomCenters.size() >= numberOfRooms) break;
        }
    }

    private void generateRoomsWithRandomStrategy() {
        int attempts = 0;
        int maxRandomAttempts = numberOfRooms * 75;
        while (roomCenters.size() < numberOfRooms && attempts < maxRandomAttempts) {
            attempts++;
            int roomW = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            int roomH = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            if (width <= roomW + 2 * ROOM_PADDING || height <= roomH + 2 * ROOM_PADDING) {
                if (roomW == MIN_ROOM_SIZE && roomH == MIN_ROOM_SIZE && attempts > 100) break;
                continue;
            }

            int boundX = width - roomW - 2 * ROOM_PADDING + 1;
            int boundY = height - roomH - 2 * ROOM_PADDING + 1;

            int x = ROOM_PADDING + random.nextInt(boundX);
            int y = ROOM_PADDING + random.nextInt(boundY);

            if (canPlaceRoomAtWithPadding(x, y, roomW, roomH)) {
                createRoom(x, y, roomW, roomH);
                roomCenters.add(new Point(x + roomW / 2, y + roomH / 2));
            }
        }
    }

    private boolean canPlaceRoomAtWithPadding(int x, int y, int roomWidth, int roomHeight) {
        int paddedStartX = x - Map.ROOM_PADDING;
        int paddedStartY = y - Map.ROOM_PADDING;
        int paddedEndX = x + roomWidth + Map.ROOM_PADDING;
        int paddedEndY = y + roomHeight + Map.ROOM_PADDING;

        if (paddedStartX < 0 || paddedStartY < 0 || paddedEndX > this.width || paddedEndY > this.height) {
            return false;
        }

        for (int curY = paddedStartY; curY < paddedEndY; curY++) {
            for (int curX = paddedStartX; curX < paddedEndX; curX++) {
                if (rooms[curY][curX].getType() != Room.RoomType.WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addEventRooms() {
        if (!(this.width > 0 && this.height > 0)) return;

        List<Point> availableRoomTiles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = rooms[y][x];
                if (room != null && room.getType() == Room.RoomType.ROOM) {
                    boolean isStartTile = (this.startPosition != null && x == this.startPosition.x && y == this.startPosition.y);
                    boolean isBossTile = (this.bossPosition != null && x == this.bossPosition.x && y == this.bossPosition.y);
                    if (!isStartTile && !isBossTile) {
                        availableRoomTiles.add(new Point(x,y));
                    }
                }
            }
        }

        if (availableRoomTiles.isEmpty()) {
            return;
        }

        int eventRoomsTarget = Math.max(1, (width * height) / 30);
        eventRoomsTarget = Math.min(eventRoomsTarget, 30);

        Collections.shuffle(availableRoomTiles, random);
        int eventsPlaced = 0;
        for (int i = 0; i < availableRoomTiles.size() && eventsPlaced < eventRoomsTarget; i++) {
            Point eventPos = availableRoomTiles.get(i);
            Room targetCell = getRoom(eventPos.x, eventPos.y);
            if (targetCell != null && targetCell.getType() == Room.RoomType.ROOM) {
                targetCell.setType(Room.RoomType.EVENT);
                eventsPlaced++;
            }
        }
    }

    private boolean canPlaceRoomAt(int x, int y, int roomWidth, int roomHeight) {
        if (x < 0 || y < 0 || x + roomWidth > this.width || y + roomHeight > this.height) {
            return false;
        }
        for (int dy = 0; dy < roomHeight; dy++) {
            for (int dx = 0; dx < roomWidth; dx++) {
                if (rooms[y + dy][x + dx].getType() != Room.RoomType.WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    private void fillWithWalls() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rooms[y][x] = new Room(Room.RoomType.WALL);
            }
        }
    }

    private void createFallbackRoom() {
        int rW = Math.max(MIN_ROOM_SIZE, Math.min(this.width - 2*ROOM_PADDING -2, MAX_ROOM_SIZE));
        int rH = Math.max(MIN_ROOM_SIZE, Math.min(this.height - 2*ROOM_PADDING -2, MAX_ROOM_SIZE));
        int centralX_padded = ROOM_PADDING + random.nextInt(Math.max(1,this.width - 2*ROOM_PADDING - rW +1));
        int centralY_padded = ROOM_PADDING + random.nextInt(Math.max(1,this.height - 2*ROOM_PADDING - rH +1));

        if (canPlaceRoomAtWithPadding(centralX_padded, centralY_padded, rW, rH)) {
            createRoom(centralX_padded, centralY_padded, rW, rH);
            Point centerFallback = new Point(centralX_padded + rW / 2, centralY_padded + rH / 2);
            roomCenters.add(centerFallback);
            if (this.startPosition == null) this.startPosition = centerFallback;

        } else {
            int centralX = Math.max(0, (this.width - rW) / 2);
            int centralY = Math.max(0, (this.height - rH) / 2);
            if (canPlaceRoomAt(centralX, centralY, rW, rH)) {
                createRoom(centralX, centralY, rW, rH);
                Point centerFallback = new Point(centralX + rW / 2, centralY + rH / 2);
                roomCenters.add(centerFallback);
                if (this.startPosition == null) this.startPosition = centerFallback;
            }
        }
    }

    private void createRoom(int x, int y, int roomWidth, int roomHeight) {
        for (int dy = 0; dy < roomHeight; dy++) {
            for (int dx = 0; dx < roomWidth; dx++) {
                if ((y + dy < height) && (x + dx < width) && (y+dy >=0) && (x+dx >=0)) {
                    rooms[y + dy][x + dx] = new Room(Room.RoomType.ROOM);
                }
            }
        }
    }

    private void connectRooms() {
        if (roomCenters.size() < 2) return;

        List<Point> connected = new ArrayList<>();
        List<Point> unconnected = new ArrayList<>(roomCenters);
        Collections.shuffle(unconnected, random);

        if (unconnected.isEmpty()) return;
        connected.add(unconnected.remove(0));

        while (!unconnected.isEmpty()) {
            Point closest = null;
            Point connectTo = null;
            double minDistance = Double.MAX_VALUE;

            for (Point currentConnectedRoom : connected) {
                for (Point currentUnconnectedRoom : unconnected) {
                    double distance = getDistance(currentConnectedRoom, currentUnconnectedRoom);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closest = currentUnconnectedRoom;
                        connectTo = currentConnectedRoom;
                    }
                }
            }

            if (closest != null) {
                createCorridor(connectTo, closest);
                connected.add(closest);
                unconnected.remove(closest);
            } else {
                break;
            }
        }
        addExtraCorridors();
    }


    private void createCorridor(Point from, Point to) {
        int currentX = from.x;
        int currentY = from.y;

        while (currentX != to.x || currentY != to.y) {
            boolean moveHorizontally = Math.abs(to.x - currentX) > Math.abs(to.y - currentY);
            if (Math.abs(to.x - currentX) == Math.abs(to.y - currentY)) {
                moveHorizontally = random.nextBoolean();
            }

            if (rooms[currentY][currentX].getType() == Room.RoomType.WALL) {
                rooms[currentY][currentX].setType(Room.RoomType.ROOM);
            }

            if (moveHorizontally && currentX != to.x) {
                currentX += Integer.signum(to.x - currentX);
            } else if (currentY != to.y) {
                currentY += Integer.signum(to.y - currentY);
            }
        }
    }

    private void addExtraCorridors() {
        if (roomCenters.isEmpty() || roomCenters.size() < 2) return;
        int extraCorridors = Math.max(1, roomCenters.size() / 10);

        for (int i = 0; i < extraCorridors; i++) {
            Point room1 = roomCenters.get(random.nextInt(roomCenters.size()));
            Point room2 = roomCenters.get(random.nextInt(roomCenters.size()));
            if (!room1.equals(room2)) {
                if(getDistance(room1, room2) > (MAX_ROOM_SIZE + 2*ROOM_PADDING) * 2) {
                    createCorridor(room1, room2);
                }
            }
        }
    }

    private double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    private void placeSpecialRooms() {
        if (roomCenters.isEmpty()) {
            ensureValidStartPosition();
            if(startPosition == null && (width > 0 && height > 0)) {
                startPosition = new Point(0,0);
                Room r = getRoom(0,0); if (r==null) rooms[0][0] = new Room(Room.RoomType.WALL);
                getRoom(0,0).setType(Room.RoomType.START);
            } else if (startPosition == null) {
                startPosition = new Point(0,0);
            }
            if (roomCenters.isEmpty()) {
                bossPosition = startPosition;
                Room bossRoomObj = getRoom(bossPosition.x, bossPosition.y);
                if (bossRoomObj != null) bossRoomObj.setType(Room.RoomType.BOSS);
                else if (width > 0 && height > 0) { rooms[bossPosition.y][bossPosition.x].setType(Room.RoomType.BOSS); }
                return;
            }
        }

        Collections.shuffle(roomCenters, random);
        Point tentativeStartPosition = roomCenters.get(0);
        this.startPosition = findNearestPassableTile(tentativeStartPosition.x, tentativeStartPosition.y, Room.RoomType.ROOM);
        if(this.startPosition == null) this.startPosition = findNearestPassableTile(tentativeStartPosition.x, tentativeStartPosition.y, null);

        if (this.startPosition == null) {
            ensureValidStartPosition();
        } else {
            Room finalStartRoom = getRoom(this.startPosition.x, this.startPosition.y);
            if(finalStartRoom != null) finalStartRoom.setType(Room.RoomType.START);
        }

        if (roomCenters.size() > 1) {
            Point farthest = null;
            double maxDistance = -1;

            for(Point center : roomCenters) {
                if(this.startPosition != null && !center.equals(this.startPosition)) {
                    double distance = getDistance(this.startPosition, center);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        farthest = center;
                    }
                }
            }
            if (farthest == null) {
                farthest = roomCenters.get(roomCenters.size()-1);
            }


            Point tentativeBossPosition = farthest;
            this.bossPosition = findNearestPassableTile(tentativeBossPosition.x, tentativeBossPosition.y, Room.RoomType.ROOM);
            if(this.bossPosition == null) this.bossPosition = findNearestPassableTile(tentativeBossPosition.x, tentativeBossPosition.y, null);

            if (this.bossPosition == null || (this.bossPosition.equals(this.startPosition)) ) {
                boolean foundDifferentBoss = false;
                for(Point center : roomCenters) {
                    if(!center.equals(this.startPosition)) {
                        Point potentialBoss = findNearestPassableTile(center.x, center.y, Room.RoomType.ROOM);
                        if (potentialBoss == null) potentialBoss = findNearestPassableTile(center.x, center.y, null);

                        if (potentialBoss != null && (!potentialBoss.equals(this.startPosition))) {
                            this.bossPosition = potentialBoss;
                            foundDifferentBoss = true;
                            break;
                        }
                    }
                }
                if(!foundDifferentBoss && this.startPosition != null) {
                    this.bossPosition = this.startPosition;
                } else if (!foundDifferentBoss) {
                    this.bossPosition = new Point(width-1, height-1);
                    Room r = getRoom(width-1, height-1); if(r==null && width > 0 && height > 0) rooms[height-1][width-1]= new Room(Room.RoomType.WALL);
                }
            }

            Room finalBossRoom = getRoom(this.bossPosition.x, this.bossPosition.y);
            if(finalBossRoom != null) finalBossRoom.setType(Room.RoomType.BOSS);

        } else if (this.startPosition != null) {
            this.bossPosition = this.startPosition;
            Room bossRoomObj = getRoom(this.bossPosition.x, this.bossPosition.y);
            if (bossRoomObj != null) {
                bossRoomObj.setType(Room.RoomType.BOSS);
            }
        } else {
            this.bossPosition = new Point(0,0);
        }
    }

    private Point findNearestPassableTile(int cx, int cy, Room.RoomType preferredType) {
        if (cx >= 0 && cx < width && cy >= 0 && cy < height) {
            Room centerRoom = rooms[cy][cx];
            if (centerRoom != null) {
                if (centerRoom.getType() == preferredType) return new Point(cx,cy);
                if (preferredType == null && centerRoom.isPassable()) return new Point(cx,cy);
            }
        }

        for (int r = 1; r < Math.max(MAX_ROOM_SIZE + 2, 7) ; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) != r && Math.abs(dy) != r) continue;

                    int nx = cx + dx;
                    int ny = cy + dy;

                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        Room room = rooms[ny][nx];
                        if (room != null) {
                            if (room.getType() == preferredType) return new Point(nx, ny);
                            if (preferredType == null && room.isPassable()) return new Point(nx,ny);
                        }
                    }
                }
            }
        }

        if(preferredType != null){
            for (int r = 1; r < Math.max(MAX_ROOM_SIZE + 2, 7) ; r++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        if (Math.abs(dx) != r && Math.abs(dy) != r) continue;
                        int nx = cx + dx;
                        int ny = cy + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            Room room = rooms[ny][nx];
                            if (room != null && room.isPassable()) return new Point(nx,ny);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void addTreasures() {
        if (!(this.width > 0 && this.height > 0)) return;

        List<Point> availableRoomTiles = new ArrayList<>();
        for (int y_coord = 0; y_coord < height; y_coord++) {
            for (int x_coord = 0; x_coord < width; x_coord++) {
                Room room = rooms[y_coord][x_coord];
                if (room != null && room.getType() == Room.RoomType.ROOM) {
                    boolean isStartTile = (this.startPosition != null && x_coord == this.startPosition.x && y_coord == this.startPosition.y);
                    boolean isBossTile = (this.bossPosition != null && x_coord == this.bossPosition.x && y_coord == this.bossPosition.y);
                    if (!isStartTile && !isBossTile) {
                        availableRoomTiles.add(new Point(x_coord,y_coord));
                    }
                }
            }
        }

        if (availableRoomTiles.isEmpty() && !roomCenters.isEmpty()) {
            for (Point center : roomCenters) {
                Room potentialTreasureRoom = getRoom(center.x, center.y);
                if (potentialTreasureRoom != null &&
                        (potentialTreasureRoom.getType() == Room.RoomType.ROOM)) {
                    boolean isStart = (this.startPosition != null && center.x == this.startPosition.x && center.y == this.startPosition.y);
                    boolean isBoss = (this.bossPosition != null && center.x == this.bossPosition.x && center.y == this.bossPosition.y);
                    if(!isStart && !isBoss) availableRoomTiles.add(center);
                }
            }
        }

        if (availableRoomTiles.isEmpty()) {
            return;
        }

        int treasureRoomsTarget = Math.max(1, availableRoomTiles.size() / 20);
        treasureRoomsTarget = Math.min(treasureRoomsTarget, Math.max(1,(width*height)/250) );
        treasureRoomsTarget = Math.min(treasureRoomsTarget, 3);

        Collections.shuffle(availableRoomTiles, random);
        int treasuresPlaced = 0;
        for (int i = 0; i < availableRoomTiles.size() && treasuresPlaced < treasureRoomsTarget; i++) {
            Point treasurePos = availableRoomTiles.get(i);
            Room targetCell = getRoom(treasurePos.x, treasurePos.y);
            if (targetCell != null && (targetCell.getType() == Room.RoomType.ROOM)){
                targetCell.setType(Room.RoomType.TREASURE);
                treasuresPlaced++;
            }
        }
    }

    private void populateRoomsWithEnemiesAndItems() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room currentRoom = rooms[y][x];
                if (currentRoom == null || !currentRoom.isPassable()) {
                    continue;
                }
                Room.RoomType type = currentRoom.getType();
                if (type == Room.RoomType.START || type == Room.RoomType.BOSS || type == Room.RoomType.TREASURE || type == Room.RoomType.WALL || type == Room.RoomType.EVENT || type == Room.RoomType.MERCHANT) {
                    continue;
                }
                boolean placedEnemy = false;
                if (random.nextDouble() < ENEMY_SPAWN_CHANCE_IN_ROOM) {
                    if (!hasAdjacentEnemy(x, y)) {
                        currentRoom.setEnemy(currentRoom.createRandomEnemy());
                        placedEnemy = true;
                    } else {
                        if (currentRoom.getItem() == null && random.nextDouble() < ITEM_SPAWN_CHANCE_IF_ENEMY_BLOCKED) {
                            currentRoom.setItem(currentRoom.createRandomItem());
                        }
                    }
                }
                if (!placedEnemy && currentRoom.getItem() == null) {
                    if (random.nextDouble() < ITEM_SPAWN_CHANCE_IF_NO_ENEMY) {
                        currentRoom.setItem(currentRoom.createRandomItem());
                    }
                }
            }
        }
    }

    private boolean hasAdjacentEnemy(int x, int y) {
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
        for (int i = 0; i < dx.length; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                Room neighbor = rooms[ny][nx];
                if (neighbor != null && neighbor.getEnemy() != null && neighbor.getEnemy().isAlive()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ensureValidStartPosition() {
        if (this.startPosition == null) {
            Point foundPassable = findNearestPassableTile(width / 2, height / 2, null);
            if (foundPassable != null) {
                this.startPosition = foundPassable;
            } else {
                this.startPosition = new Point(0,0);
                if (getRoom(0,0) == null && width > 0 && height > 0) rooms[0][0] = new Room(Room.RoomType.WALL);
            }
        }

        Room startRoom = getRoom(startPosition.x, startPosition.y);
        if (startRoom == null || !startRoom.isPassable()) {
            Point newValidStart = findNearestPassableTile(startPosition.x, startPosition.y, null);
            if (newValidStart != null) {
                this.startPosition = newValidStart;
                startRoom = getRoom(this.startPosition.x, this.startPosition.y);
            } else {
                this.startPosition = new Point(0,0);
                if(width > 0 && height > 0) {
                    if(rooms[0][0] == null) rooms[0][0] = new Room(Room.RoomType.WALL);
                    rooms[0][0].setType(Room.RoomType.START);
                }
                return;
            }
        }
        if (startRoom != null) startRoom.setType(Room.RoomType.START);
    }

    public Room getRoom(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return rooms[y][x];
        }
        return null;
    }

    public boolean isValidPosition(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        Room room = rooms[y][x];
        return room != null && room.isPassable();
    }

    public Point getStartPosition() {
        if (startPosition == null) {
            ensureValidStartPosition();
            if (startPosition == null ) {
                startPosition = new Point(0,0);
            }
        }
        return new Point(startPosition.x, startPosition.y);
    }
    private void addMerchant() {
        if (!(this.width > 0 && this.height > 0) || roomCenters.size() < 5) {
            return;
        }

        List<Point> availableRoomTiles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = rooms[y][x];
                if (room != null && room.getType() == Room.RoomType.ROOM) {
                    boolean isStartTile = (this.startPosition != null && x == this.startPosition.x && y == this.startPosition.y);
                    boolean isBossTile = (this.bossPosition != null && x == this.bossPosition.x && y == this.bossPosition.y);
                    if (!isStartTile && !isBossTile) {
                        availableRoomTiles.add(new Point(x,y));
                    }
                }
            }
        }

        if (availableRoomTiles.isEmpty()) {
            return;
        }


        Collections.shuffle(availableRoomTiles, random);
        Point merchantPos = availableRoomTiles.get(0);
        Room targetCell = getRoom(merchantPos.x, merchantPos.y);
        if (targetCell != null && targetCell.getType() == Room.RoomType.ROOM) {
            targetCell.setType(Room.RoomType.MERCHANT);
        }
    }


    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Room[][] getRooms() { return rooms; }
}