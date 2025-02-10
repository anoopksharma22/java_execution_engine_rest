import {useState} from "react";
import Editor from "@monaco-editor/react";
import {RiResetLeftLine} from "react-icons/ri";
import "./code_editor.css";
const CodeEditor = () => {
    const [code, setCode] = useState(`// Do not rename to class name
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}`);
    const [output, setOutput] = useState("Waiting for output...");
    const [loading, setLoading] = useState(false);
    const handleReset = async () => {
        setCode(`// Do not rename to class name
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}`);
        setOutput("Waiting for output...");

    };
    const handleRunCode = async () => {
        setLoading(true);
        setOutput("Running...");

        try {
            const response = await fetch("http://localhost:8080/api/execute", {
                method: "POST",
                headers: { "Content-Type": "text/plain" },
                body: code
            });

            const result = await response.json();
            if( result.output !== "" ) {
                setOutput(result.output || "No output returned");
            }else{
                setOutput(result.error || "No output returned");
            }

        } catch (error) {
            setOutput(`Error: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <div className="editor-container">
                <div className="code-editor">
                <p className="title">Java code editor</p>
                    <div className="editor">
                        <Editor
                            height="100%"
                            defaultLanguage="java"
                            value={code}
                            theme="vs-dark"
                            options={{
                                fontSize: "16px", // Set font size dynamically
                            }}
                            onChange={(value) => setCode(value)}
                        />
                        <div className="editor-buttons">
                            <RiResetLeftLine onClick={handleReset} className="reset-button" />
                            <button className="run-button" onClick={handleRunCode} disabled={loading}>
                                {loading ? "Running..." : "Run Code"}
                            </button>
                        </div>
                    </div>
                </div>

                <div className="output">
                    <p>Output:</p>
                    <pre className="output-text">
                        {output}
                    </pre>
                </div>
            </div>
        </>
    );
}

export default CodeEditor;