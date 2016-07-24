using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Repositories
{
    public interface IPointRepository : IDisposable
    {
        IQueryable<Point> Get();
        IQueryable<Point> Get(int examId, int stepNum);
        IQueryable<Point> Get(int examId, int stepNum, decimal x);
        Task<Point> CreateAsync(Point point);
        Task<int> UpdateAsync(Point point);
        Task<int> DeleteAsync(Point point);
        Task<Point> FindAsync(int examId, int stepNum, decimal x);
        Task<int> SaveRepositoryAsync();
    }
}
