import React from 'react';
import './SearchBar.css';

type SearchBarProps = {
    searchTerm: string;
    setSearchTerm: (term: string) => void;
    results: { name: string, id: number }[];
    onSelect: (id: number) => void;
};

export function SearchBar({ searchTerm, setSearchTerm, results, onSelect }: SearchBarProps) {
    return (
        <div className="search-container">
            <input
                type="text"
                className="search-input"
                placeholder="Search by road name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
            {results.length > 0 && searchTerm && (
                <div className="search-results">
                    {results.map((result) => (
                        <div key={result.id} onClick={() => onSelect(result.id)}>
                            {result.name}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
