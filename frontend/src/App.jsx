import "./App.css";

import CodeEditor from "./components/CodeEditor.jsx";
import Navbar from "./components/Navbar.jsx";

const App = () => {
    return(
        <div className="main-container">
            <Navbar />
            <CodeEditor/>
        </div>
    );

};

export default App;
