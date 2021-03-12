import java.util.Random;

public class MySkipList<T extends Comparable<? super T>> {

	final static int LEVELS = 5;

	private final SkipNode<T> head = new SkipNode<>(null);
	private final Random rand = new Random();
	

	public void add(T data) {
		SkipNode<T> skipNode = new SkipNode<>(data);
		for (int i = 0; i < LEVELS; i++) {
			if (rand.nextInt((int) Math.pow(2, i)) == 0) {
				// insert with prob = 1/(2^i)
				add(skipNode, i);
			}
		}
	}

	public boolean delete(T target) {
		System.out.println("Deleting " + target);
		SkipNode<T> deleteNode = find(target, true);
		if (deleteNode == null)
			return false;
		deleteNode.data = null;

		for (int i = 0; i < LEVELS; i++) {
			head.refreshAfterDelete(i);
		}

		System.out.println("deleted...");
		return true;
	}

	public SkipNode<T> find(T data) {
		return find(data, true);
	}

	private void add(SkipNode<T> SkipNode, int level) {
		head.add(SkipNode, level);
	}

	private SkipNode<T> find(T data, boolean print) {
		SkipNode<T> result = null;
		for (int i = LEVELS - 1; i >= 0; i--) {
			if ((result = head.find(data, i, print)) != null) {
				if (print) {
					System.out.println("Found " + data.toString() + " at level " + i);
					System.out.println();
				}
				break;
			}
		}

		return result;
	}

	public static void main(String[] args) {
		MySkipList<Integer> sl = new MySkipList<>();
		int[] data = { 4, 2, 7, 0, 9, 1, 3, 7, 3, 4, 5, 6, 0, 2, 8 };
		for (int i : data) {
			sl.add(i);
		}
		sl.find(4);
		sl.delete(4);
		System.out.println("Inserting 10");
		sl.add(10);
		sl.find(10);
	}

	private class SkipNode<N extends Comparable<? super N>> {

		N data;
		SkipNode<N>[] next = (SkipNode<N>[]) new SkipNode[LEVELS];

		SkipNode(N data) {
			this.data = data;
		}

		void refreshAfterDelete(int level) {
			SkipNode<N> currentNode = this;
			while (currentNode != null && currentNode.getNext(level) != null) {
				if (currentNode.getNext(level).data == null) {
					SkipNode<N> nextNode = currentNode.getNext(level).getNext(level);
					currentNode.setNext(nextNode, level);
					return;
				}

				currentNode = currentNode.getNext(level);
			}
		}

		void setNext(SkipNode<N> next, int level) {
			this.next[level] = next;
		}

		SkipNode<N> getNext(int level) {
			return this.next[level];
		}

		SkipNode<N> find(N data, int level, boolean print) {
			if (print) {
				System.out.print("Searching for: " + data + " at ");
			}

			SkipNode<N> result = null;
			SkipNode<N> currentNode = this.getNext(level);
			while (currentNode != null && currentNode.data.compareTo(data) < 1) {
				if (currentNode.data.equals(data)) {
					result = currentNode;
					break;
				}

				currentNode = currentNode.getNext(level);
			}

			return result;
		}

		void add(SkipNode<N> skipNode, int level) {
			SkipNode<N> currentNode = this.getNext(level);
			if (currentNode == null) {
				this.setNext(skipNode, level);
				return;
			}

			if (skipNode.data.compareTo(currentNode.data) < 1) {
				this.setNext(skipNode, level);
				skipNode.setNext(currentNode, level);
				return;
			}

			while (currentNode.getNext(level) != null && currentNode.data.compareTo(skipNode.data) < 1
					&& currentNode.getNext(level).data.compareTo(skipNode.data) < 1) {

				currentNode = currentNode.getNext(level);
			}

			SkipNode<N> nextNode = currentNode.getNext(level);
			currentNode.setNext(skipNode, level);
			skipNode.setNext(nextNode, level);
		}

	}

}
