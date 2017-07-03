package jp.or.venuspj.remoting.urlBase.config;

import jp.or.venuspj.remoting.urlBase.UrlBaseConnector;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TargetSpecificURLBasedConnector extends UrlBaseConnector {
    // constants
    /**
     * リモートオブジェクトのURLをキャッシュする上限のデフォルト値
     */
    protected static final int DEFAULT_MAX_CACHED_URLS = 10;

    protected LRUMap cachedURLs = new LRUMap(DEFAULT_MAX_CACHED_URLS);
    public TargetSpecificURLBasedConnector(UrlBaseServiceConfig anUrlBaseServiceConfig) {
        super(anUrlBaseServiceConfig);
    }

    /**
     * リモートオブジェクトのURLをキャッシュする上限を設定します。
     *
     * @param maxCachedURLs
     *            リモートオブジェクトのURLをキャッシュする上限です
     */
    public synchronized void setMaxCachedURLs(final int maxCachedURLs) {
        cachedURLs.setMaxSize(maxCachedURLs);
    }

    /**
     * ターゲットとなるリモートオブジェクトのURLを解決し、サブクラス固有の方法でメソッド呼び出しを実行します。
     *
     * @param remoteName
     *            リモートオブジェクトの名前
     * @param method
     *            呼び出すメソッド
     * @param args
     *            リモートオブジェクトのメソッド呼び出しに渡される引数値を格納するオブジェクト配列
     * @return リモートオブジェクトに対するメソッド呼び出しからの戻り値
     * @throws Throwable
     *             リモートオブジェクトに対するメソッド呼び出しからスローされた例外です
     */
    public Object invoke(final String remoteName, final Method method, final Object[] args)
            throws Throwable {
        return invoke(getTargetURL(remoteName), method, args);
    }

    /**
     * ターゲットとなるリモートオブジェクトのURLを返します。
     * リモートオブジェクトのURLは、リモートオブジェクトの名前をベースURLからの相対URLとして解決します。
     *
     * @param remoteName
     *            ターゲットとなるリモートオブジェクトの名前
     * @return ターゲットとなるリモートオブジェクトのURL
     * @throws MalformedURLException
     *             ベースURLとリモートオブジェクトの名前からURLを作成できなかった場合にスローされます
     */
    protected synchronized URL getTargetURL(final String remoteName) throws MalformedURLException {
        URL targetURL = (URL) cachedURLs.get(remoteName);
        if (targetURL == null) {
            targetURL = new URL(urlBaseServiceConfig.baseUrl().asUrl(), remoteName);
            cachedURLs.put(remoteName, targetURL);
        }
        return targetURL;
    }

    /**
     * サブクラス固有の方法でリモートメソッドの呼び出しを実行し、その結果を返します。
     *
     * @param targetURL
     *            ターゲットとなるリモートオブジェクトのURL
     * @param method
     *            呼び出すメソッド
     * @param args
     *            リモートオブジェクトのメソッド呼び出しに渡される引数値を格納するオブジェクト配列
     * @return リモートオブジェクトに対するメソッド呼び出しからの戻り値
     * @throws Throwable
     *             リモートオブジェクトに対するメソッド呼び出しからスローされた例外です
     */
    protected abstract Object invoke(URL targetURL, Method method, Object[] args) throws Throwable;

    /**
     * LRUマップ <br>
     * エントリ数に上限があり、それを超えてエントリが追加された場合にはもっとも使用されていないエントリが取り除かれるマップの実装です。
     * エントリ数の上限は随時増やすことが出来ますが、減らしてもその数までエントリが取り除かれることはありません。 このマップは同期されません。
     */
    protected static class LRUMap extends LinkedHashMap {

        private static final long serialVersionUID = 1L;

        /** デフォルト初期容量 */
        protected static final int DEFAULT_INITIAL_CAPACITY = 16;

        /** デフォルト負荷係数 */
        protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

        protected int maxSize;

        /**
         * デフォルトの初期容量と負荷係数で指定されたエントリ数を上限とするインスタンスを構築します。
         *
         * @param maxSize
         *            エントリ数の最大数
         */
        public LRUMap(final int maxSize) {
            this(maxSize, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
        }

        /**
         * 指定された初期容量と負荷係数、エントリ数の上限を持つインスタンスを構築します。
         *
         * @param maxSize
         *            エントリ数の最大数
         * @param initialCapacity
         *            初期容量
         * @param loadFactor
         *            負荷係数
         */
        public LRUMap(final int maxSize, final int initialCapacity, final float loadFactor) {
            super(initialCapacity, loadFactor, true);
            this.maxSize = maxSize;
        }

        /**
         * エントリ数の最大値を設定します。
         *
         * @param maxSize
         *            エントリ数の最大数
         */
        public void setMaxSize(final int maxSize) {
            this.maxSize = maxSize;
        }

        /**
         * マップのエントリ数が最大数を超えている場合 <code>true</code> を返します。
         * その結果、最も前にマップに挿入されたエントリがマップから削除されます。
         *
         * @param eldest
         *            もっとも前にマップに挿入されたエントリ
         * @return マップのエントリ数が最大数を超えている場合 <code>true</code>
         */
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return maxSize == 0 || size() > maxSize;
        }
    }
}
