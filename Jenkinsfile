pipeline {
    agent any
    // 定数や変数を定義する
    environment {
        reportDir = 'build/reports'
        javaDir = 'src/main/java'
        resourcesDir = 'src/main/resources'
        testReportDir = 'build/test-results/test'
        jacocoReportDir = 'build/jacoco'
        javadocDir = 'build/docs/javadoc'
        libsDir = 'build/libs'
        appName = 'studio-web'
        appVersion = '1.0.0-SNAPSHOT'
    }

    // stagesブロック中に一つ以上のstageを定義する
    stages {
        stage('事前準備') {
            // 実際の処理はstepsブロック中に定義する
            steps {
                deleteDir()
                // このJobをトリガーしてきたGithubのプロジェクトをチェックアウト
                checkout scm
                // ジョブ失敗の原因調査用にJenkinsfileとbuild.gradleは最初に保存する
                archiveArtifacts "Jenkinsfile"
                archiveArtifacts "build.gradle"
                archiveArtifacts "settings.gradle"
                // scriptブロックを使うと従来のScripted Pipelinesの記法も使える
                script {
                    // Permission deniedで怒られないために実行権限を付与する
                    if(isUnix()) {
                        sh 'chmod +x gradlew'
                    }
                }
                gradlew 'clean'
            }
        }

        stage('コンパイル') {
            steps {
                gradlew 'classes testClasses'
            }

            // postブロックでstepsブロックの後に実行される処理が定義できる
            post {
                // alwaysブロックはstepsブロックの処理が失敗しても成功しても必ず実行される
                always {
                    // JavaDoc生成時に実行するとJavaDocの警告も含まれてしまうので
                    // Javaコンパイル時の警告はコンパイル直後に収集する
                    step([
                        // プラグインを実行するときのクラス指定は完全修飾名でなくてもOK
                        $class: 'WarningsPublisher',
                        // Job実行時のコンソールから警告を収集する場合はconsoleParsers、
                        // pmd.xmlなどのファイルから収集する場合はparserConfigurationsを指定する。
                        // なおparserConfigurationsの場合はparserNameのほかにpattern(集計対象ファイルのパス)も指定が必要
                        // パーサ名は下記プロパティファイルに定義されているものを使う
                        // https://github.com/jenkinsci/warnings-plugin/blob/master/src/main/resources/hudson/plugins/warnings/parser/Messages.properties
                        consoleParsers: [
                            [parserName: 'Java Compiler (javac)'],
                        ],
                        canComputeNew: false,
                        canResolveRelativesPaths: false,
                        usePreviousBuildAsReference: true
                    ])
                }
            }
        }

        stage('静的コード解析') {
            steps {
                // 並列処理の場合はparallelメソッドを使う
                parallel(
                    '静的コード解析sub' : {
                    gradlew 'check -x test'
                        // dirメソッドでカレントディレクトリを指定できる
                        findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs/*.xml', unHealthy: ''
                        pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/pmd/*.xml', unHealthy: ''
                        dry canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/cpd/*.xml', unHealthy: ''
                        archiveArtifacts "**/findbugs/*.xml"
                        archiveArtifacts "**/pmd/*.xml"
                        archiveArtifacts "**/cpd/*.xml"
                        archiveArtifacts "**/jdepend/*.xml"
                    },
                    'タスクスキャン': {
                        step([
                            $class: 'TasksPublisher',
                            pattern: './**',
                            // 集計対象を検索するときに大文字小文字を区別するか
                            ignoreCase: true,
                            // 優先度別に集計対象の文字列を指定できる
                            // 複数指定する場合はカンマ区切りの文字列を指定する
                            high: 'FIXME',
                            normal: 'TODO',
                            low: 'XXX',
                        ])
                    }
                )
            }
        }
        stage('テスト') {
            steps {
                gradlew 'test jacocoTestReport -x classes -x testClasses'
                junit allowEmptyResults: true, testResults: "**/${testReportDir}/*.xml"
                archiveArtifacts allowEmptyArchive: true, artifacts: "**/${testReportDir}/*.xml"
                // カバレッジレポートを生成（テストクラスを除外）
                echo 'JacocoReportアーカイブ 開始'
                jacoco exclusionPattern: '**/*Test*.class'
                echo 'JacocoReportアーカイブ 終了'
            }
        }
    }

    // stagesブロックと同じレベルにpostブロックを定義すると
    // 全てのstage処理が終わった後の処理の定義が可能
    post {
        always {
            // 最後にワークスペースの中身を削除
            deleteDir()
        }
    }
}

// Gradlewコマンドを実行する
def gradlew(command) {
    if(isUnix()) {
            sh "./gradlew ${command} --stacktrace --daemon"
        } else {
            bat "./gradlew.bat ${command} --stacktrace  --daemon"
    }
}