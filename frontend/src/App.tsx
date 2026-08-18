import Board from "./components/Board";
import "./App.css";

function App() {
  return (
    <div className="app">
      <header className="topbar">
        <h1>TaskManagement</h1>
      </header>
      <Board />
    </div>
  );
}

export default App;
