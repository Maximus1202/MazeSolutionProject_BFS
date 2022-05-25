
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_BFS:
                    result = BFS();
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }

    public boolean BFS() {
        java.util.Queue<Node> nodes = new LinkedList<>();
        nodes.add(new Node(this.startRow,this.startColumn));
        while (!nodes.isEmpty()) {
            Node currentNode = nodes.remove();
            if (!isVisited(currentNode)) {
                this.visited[currentNode.getRow()][currentNode.getColumn()] = true;
                setSquareAsVisited(currentNode.getRow(),currentNode.getColumn(),true);
                if (currentNode.getRow() == this.values.length-1 && currentNode.getColumn() == this.values.length-1) {
                    return true;
                }
                java.util.List<Node> currentNeighbors = getNeighbors(currentNode);
                for (Node neighbor : currentNeighbors) {
                    if (!isVisited(neighbor)) {
                        nodes.add(neighbor);
                    }
                }
            }
        }
        return false;
    }

    public boolean isVisited(Node node) {
        return this.visited[node.getRow()][node.getColumn()];
    }
    public java.util.List<Node> getNeighbors(Node currentNode) {
        java.util.List<Node> neighbors = new ArrayList<>();
        neighbors.add(new Node(currentNode.getRow(),currentNode.getColumn()+1));
        neighbors.add(new Node(currentNode.getRow(),currentNode.getColumn()-1));
        neighbors.add(new Node(currentNode.getRow()-1,currentNode.getColumn()));
        neighbors.add(new Node(currentNode.getRow()+1,currentNode.getColumn()));
        checkBoundsAndObstacles(neighbors);
        return neighbors;
    }
    public void checkBoundsAndObstacles(java.util.List<Node> neighbors) {
        List<Node> nodesToRemove = new ArrayList<>();
        for (Node node : neighbors) {
            if ((node.getRow()<0) || (node.getRow()>this.values.length-1) || (node.getColumn()<0) || (node.getColumn()>this.values.length-1) || (this.values[node.getRow()][node.getColumn()] == Definitions.OBSTACLE)){
                nodesToRemove.add(node);
            }
        }
        neighbors.removeAll(nodesToRemove);
    }


    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
