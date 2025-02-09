import "./navbar.css";
import logo from "../assets/goducky_logo.png";

export default function Navbar() {
    return (

        <nav className="navbar">
            <div className="logo">
                <img src={logo} alt="logo"/>
                <h3>GoDucky</h3>
            </div>
            <ul className="navbar-nav">
                <li className="nav-item"><a href={"#"}>Home</a></li>
                <li className="nav-item"><a href={"#"}>Articles</a></li>
                <li className="nav-item"><a href={"#"}>How To?</a></li>
                <li className="nav-item"><a href={"#"}>Code Editor</a></li>
            </ul>
        </nav>

    );
}