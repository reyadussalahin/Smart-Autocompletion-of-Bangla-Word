import java.io.*;
import java.util.*;



/**
 * Created by Reyad on 10/14/2018.
 */



class Node {
    private String word;
    private int freq;
    private NodeMap next;

    Node() {
        this.word = null;
        this.freq = 0;
        next = new NodeMap();
    }

    public void setWord(String word) {
        this.word = word;
    }
    public void setFrequency(int freq) {
        this.freq = freq;
    }
    public String getWord() {
        return this.word;
    }
    public int getFrequency() {
        return this.freq;
    }
    public NodeMap getNext() {
        return this.next;
    }
    boolean isLeaf() {
        return (word != null);
    }
}

class NodeMap {
    private int[] key;
    private Node[] val;
    private int size;
    private int capacity;

    NodeMap() {
        this.capacity = 4;
        this.size = 0;
        this.key = new int[this.capacity];
        this.val = new Node[this.capacity];
    }

    private void resize() {
        int[] tempKey = new int[capacity];
        Node[] tempVal = new Node[capacity];
        System.arraycopy(key, 0, tempKey, 0, size);
        System.arraycopy(val, 0, tempVal, 0, size);
        key = tempKey;
        val = tempVal;
    }

    private void grow() {
        capacity = capacity * 2;
        resize();
    }

    private void shrink() {
        capacity = capacity / 2;
        resize();
    }

    private int findLeftNeighbour(int k) {
        int s = 0, e = size-1, x = -1;
        while(s <= e) {
            int m = (s+e) / 2;
            if(key[m] < k) {
                x = m;
                s = m+1;
            }
            else e = m-1;
        }
        return x;
    }

    public boolean contains(int k) {
        int at = findLeftNeighbour(k);
        return (at+1 < size && key[at+1] == k);
    }

    public boolean put(int k, Node v) {
        int at = findLeftNeighbour(k);
        if(at+1 < size && key[at+1] == k) return false;
        if(size == capacity) grow();
        for(int i=size-1; i>at; i--) {
            key[i+1] = key[i];
            val[i+1] = val[i];
        }
        key[at+1] = k;
        val[at+1] = v;
        size++;
        return true;
    }

    public boolean remove(int k) {
        int at = findLeftNeighbour(k);
        if(at+1 >= size || key[at+1] != k) return false;
        for(int i=at+1; i<size; i++) {
            key[i] = key[i+1];
            val[i] = val[i+1];
        }
        size--;
        if((2 * size) == capacity && capacity > 4) shrink();
        return true;
    }

    public Node get(int k) {
        int at = findLeftNeighbour(k);
        if(at+1 < size && key[at+1] == k) return val[at+1];
        return null;
    }

    public int keyAt(int i) {
        if(i < size) return key[i];
        return -1;
    }

    public Node valAt(int i) {
        if(i < size) return val[i];
        return null;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for(int i=0; i<size; i++) {
            sb.append(" " + (char)key[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }
}

class NodeStack {
    private Node[] buf;
    private int size;
    private int capacity;

    NodeStack() {
        this.size = 0;
        this.capacity = 10;
        this.buf = new Node[this.capacity];
    }

    private void resize() {
        Node[] temp = new Node[capacity];
        System.arraycopy(buf, 0, temp, 0, size);
        buf = temp;
    }

    private void grow() {
        capacity = capacity * 2;
        resize();
    }

    private void shrink() {
        capacity = capacity / 2;
        resize();
    }

    public void push(Node v) {
        if(size == capacity) grow();
        buf[size++] = v;
    }

    public Node pop() {
        if(size == 0) return null;
        Node v = buf[size - 1];
        size--;
        if((2 * size) == capacity && capacity > 10) shrink();
        return v;
    }

    public Node peek() {
        return (size == 0) ? null : buf[size - 1];
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return size;
    }

    public void clear() {
        this.capacity = 10;
        this.size = 0;
        this.buf = new Node[this.capacity];
    }
}


interface Job {
    boolean nullJob(Node v, int key);
    boolean endJob(Node v, String word, int freq);
}

class JobAdd implements Job {
    @Override
    public boolean nullJob(Node v, int key) {
        NodeMap next = v.getNext();
        if(next.get(key) == null) next.put(key, new Node());
        return true;
    }

    @Override
    public boolean endJob(Node v, String word, int freq) {
        if(v.isLeaf()) return false;
        v.setWord(word);
        v.setFrequency(freq);
        return true;
    }
}

class JobSetFrequency implements Job {
    @Override
    public boolean nullJob(Node v, int key) {
        return (v.getNext().get(key) != null);
    }

    @Override
    public boolean endJob(Node v, String word, int freq) {
        if(v.isLeaf()) v.setFrequency(freq);
        return v.isLeaf();
    }
}

class JobContains implements Job {
    @Override
    public boolean nullJob(Node v, int key) {
        return (v.getNext().get(key) != null);
    }

    @Override
    public boolean endJob(Node v, String word, int freq) {
        return v.isLeaf();
    }
}

class JobIsPrefix implements Job {
    @Override
    public boolean nullJob(Node v, int key) {
        return (v.getNext().get(key) != null);
    }

    @Override
    public boolean endJob(Node v, String word, int freq) {
        return true;
    }
}

public class Trie {
    private Node head;
    private Language lang;
    private int wordCount;
    private Job jobAdd = new JobAdd();
    private Job jobSetFrequency = new JobSetFrequency();
    private Job jobContains = new JobContains();
    private Job jobIsPrefix = new JobIsPrefix();
    private Comparator<Node> comp;
    private Node[] suggestionBuffer;
    private int suggestionCount;
    private TreeSet<Node> combinedBuffer;

    Trie(Language lang) {
        this.head = new Node();
        this.lang = lang;
        this.wordCount = 0;
        this.jobAdd = new JobAdd();
        this.jobSetFrequency = new JobSetFrequency();
        this.jobContains = new JobContains();
        this.jobIsPrefix = new JobIsPrefix();
        this.comp = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.getFrequency() - o1.getFrequency();
            }
        };
        this.suggestionBuffer = new Node[lang.maxPossibleWordInSuggestion()];
        this.combinedBuffer = new TreeSet<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if(o1.getFrequency() == o2.getFrequency()) return o1.getWord().compareTo(o2.getWord());
                return o2.getFrequency() - o1.getFrequency();
            }
        });
    }

    Trie(Language lang, BufferedReader br) throws IOException {
        this.head = new Node();
        this.lang = lang;
        this.wordCount = 0;
        this.jobAdd = new JobAdd();
        this.jobSetFrequency = new JobSetFrequency();
        this.jobContains = new JobContains();
        this.jobIsPrefix = new JobIsPrefix();
        this.comp = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.getFrequency() - o1.getFrequency();
            }
        };
        this.suggestionBuffer = new Node[lang.maxPossibleWordInSuggestion()];
        this.combinedBuffer = this.combinedBuffer = new TreeSet<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if(o1.getFrequency() == o2.getFrequency()) return o1.getWord().compareTo(o2.getWord());
                return o2.getFrequency() - o1.getFrequency();
            }
        });


        /* escaping default first new line */

        br.readLine();


        /* stack to help create trie from file */

        NodeStack stack = new NodeStack();


        /* buffer to contain the present word */

        char[] buffer = new char[lang.maxPossibleWordDepth()];
        int bufferSize = 0;


        /* freqBuffer to help compute frequency of a word read from file */

        char[] freqBuffer = new char[32];
        

        int c = 0;

        stack.push(head);
        while((c = br.read()) != -1) {
            if(c == ',') continue;
            else if(c == '.') {
                int cnt = 0;
                while((c = br.read()) != '.') freqBuffer[cnt++] = (char)c;
                String freqString = new String(freqBuffer, 0, cnt);
                stack.peek().setFrequency(Integer.parseInt(freqString));

                String temp = new String(buffer, 0, bufferSize);
                stack.peek().setWord(temp);

                this.wordCount++;
            }
            else if(c == '?') {
                stack.pop();
                bufferSize--;
            }
            else if(lang.startLetter() <= c && c <= lang.endLetter()) {
                buffer[bufferSize++] = (char)c;
                int at = lang.mapToInt(c);
                Node v = new Node();
                stack.peek().getNext().put(at, v);
                stack.push(v);
            }
            else {
                throw new RuntimeException();
            }
        }
    }

    public int getWordCount() {
        return this.wordCount;
    }

    private int getFrequency(Node v, String s, int len, int lev) {
        if(lev == len) {
            if(v.isLeaf()) return v.getFrequency();
            return -1;
        }
        int key = lang.mapToInt(s.charAt(lev));
        Node vNext = v.getNext().get(key);
        if(vNext == null) return -1;
        return getFrequency(vNext, s, len, lev+1);
    }

    public int getFrequency(String s) {
        return getFrequency(head, s, s.length(), 0);
    }

    private boolean doJob(Node v, String s, int len, int lev, int freq, Job job) {
        if(lev == len) return job.endJob(v, s, freq);
        int key = lang.mapToInt(s.charAt(lev));
        if(!job.nullJob(v, key)) return false;
        return doJob(v.getNext().get(key), s, len, lev+1, freq, job);
    }

    public boolean contains(String s) {
        return doJob(head, s, s.length(), 0, 0, jobContains);
    }

    public boolean add(String s) {
        boolean flag = doJob(head, s, s.length(), 0, 1, jobAdd);
        if(flag) this.wordCount++;
        return flag;
    }

    public boolean add(String s, int freq) {
        boolean flag = doJob(head, s, s.length(), 0, freq, jobAdd);
        if(flag) this.wordCount++;
        return flag;
    }

    public boolean setFrequency(String s, int freq) {
        return doJob(head, s, s.length(), 0, freq, jobSetFrequency);
    }

    public boolean updateFrequency(String s, int uFreq) {
        if(!contains(s)) return false;
        int freq = getFrequency(s) + uFreq;
        return setFrequency(s, freq);
    }

    public boolean isPrefix(String s) {
        return doJob(head, s, s.length(), 0, 0, jobIsPrefix);
    }


    /* It's better not to use this toString() function.....
    ....It's only requirement was for debugging */

    @Override
    public String toString() {
        return toString(head);
    }

    private String toString(Node v) {
        StringBuilder sb = new StringBuilder("");
        NodeMap next = v.getNext();
        for(int i=0, len = next.size(); i<len; i++) {
            int key = next.keyAt(i);
            Node val = next.valAt(i);
            char ch = (char)lang.retrieveLetter(key);
            sb.append("{" + (char)lang.retrieveLetter(key));
            if(val.isLeaf()) sb.append(".");
            sb.append(toString(val) + "}");
        }
        return sb.toString();
    }


    public void dump(BufferedWriter bw) throws IOException {
        bw.write('\n'); // putting first default new line

        dump(head, bw);

        bw.flush();
    }

    private void dump(Node v, BufferedWriter bw) throws IOException {
        NodeMap next = v.getNext();
        for(int i=0, len = next.size(); i<len; i++) {
            int key = next.keyAt(i);
            Node val = next.valAt(i);
            if(val.isLeaf())
                bw.write(((char)lang.retrieveLetter(key)) + "." + val.getFrequency() + ".");
            else bw.write(((char)lang.retrieveLetter(key)) + ",");
            dump(val, bw);
        }
        if(v != this.head) bw.write('?');
    }


    private boolean isPure(String s) {
        for(int i=0, len = s.length(); i<len; i++) {
            if(!lang.isLetter(s.charAt(i))) return false;
        }
        return true;
    }

    private void produceSuggestions(String prefix) {
        suggestionCount = 0;
        produceSuggestions(head, prefix, prefix.length(), 0);
        Arrays.sort(suggestionBuffer, 0, suggestionCount, comp);
    }

    private void produceSuggestions(Node v, String s, int len, int lev) {
        if(lev >= len) {
            if(v.isLeaf()) suggestionBuffer[suggestionCount++] = v;
            NodeMap next = v.getNext();
            for(int i=0, l = next.size(); i<l; i++) produceSuggestions(next.valAt(i), s, len, lev+1);
        }
        else {
            int at = lang.mapToInt(s.charAt(lev));
            NodeMap next = v.getNext();
            if(next.get(at) == null) return; // not found anything which matches given letter sequence
            produceSuggestions(next.get(at), s, len, lev+1);
        }
    }

    public String[] getSuggestions(String prefix, int limit) {
        if(!isPure(prefix)) return null;
        produceSuggestions(prefix);
        int size = Math.min(limit, suggestionCount);
        String[] b = new String[size];
        for(int i=0; i<size; i++) b[i] = suggestionBuffer[i].getWord();
        return b;
    }

    public String[] getSuggestions(String[] prefix, int n, int limit) {
        combinedBuffer.clear();
        for(int i=0; i<n; i++) {
            if(!isPure(prefix[i])) continue;
            produceSuggestions(prefix[i]);
            int l = Math.min(suggestionCount, limit);
            for(int h=0; h<l; h++) combinedBuffer.add(suggestionBuffer[h]);
        }
        if(combinedBuffer.size() == 0) return null;
        int size = Math.min(combinedBuffer.size(), limit);
        String[] b = new String[size];
        int i = 0;
        for(Node it: combinedBuffer) {
            b[i++] = it.getWord();
            if(i == size) break;
        }
        return b;
    }
}
