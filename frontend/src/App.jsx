import { useState } from "react";
import Editor from "@monaco-editor/react";
import "./App.css";
const App = () => {
    const [code, setCode] = useState(`public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}`);
    const [output, setOutput] = useState("Waiting for output...");
    const [loading, setLoading] = useState(false);

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
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: 20, width: "100%", maxWidth: "1200px", margin: "0 auto" }}>
            <h2>Java Code Editor</h2>
            <div style={{ width: "100%", height: "400px", border: "1px solid #ddd" }}>
                <Editor
                    height="100%"
                    defaultLanguage="java"
                    defaultValue={code}
                    theme="vs-dark"
                    onChange={(value) => setCode(value)}
                />
            </div>
            <button
                onClick={handleRunCode}
                disabled={loading}
                style={{ marginTop: 10, padding: "10px 20px", fontSize: 16, cursor: "pointer" }}
            >
                {loading ? "Running..." : "Run Code"}
            </button>
            <h3>Output:</h3>
            <pre
                style={{
                    marginTop: 10,
                    padding: 10,
                    width: "100%",
                    border: "1px solid #ddd",
                    background: "#f9f9f9",
                    minHeight: 50,
                    whiteSpace: "pre-wrap",
                    color: "#333", // Ensures the text is dark for visibility
                    fontFamily: "monospace" // For better text rendering
                }}
            >
                {output}
            </pre>
        </div>
    );
};

export default App;
